package dizzybrawl.database.services.impls;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.models.PreRegistrationAccount;
import dizzybrawl.database.services.AccountService;
import dizzybrawl.database.sql.SqlLoadable;
import dizzybrawl.database.sql.AccountSqlQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class PgAccountService implements AccountService, SqlLoadable<AccountSqlQuery> {

    private static final Logger log = LoggerFactory.getLogger(PgAccountService.class);

    private final HashMap<AccountSqlQuery, String> sqlQueries;
    private final PgPool pgClient;

    public PgAccountService(SqlClient dbClient, Handler<AsyncResult<AccountService>> readyHandler) {
        PgPool castResult = null;
        try {
            castResult = (PgPool) dbClient;
        } catch (ClassCastException e) {
            log.error("SQLClient can't be casted to PostgreSQL client.", e.getCause());
            readyHandler.handle(Future.failedFuture(e.getCause()));
        }

        this.pgClient = castResult;
        this.sqlQueries = loadSqlQueries();

        if (sqlQueries.isEmpty()) {
            readyHandler.handle(Future.failedFuture("Service doesn't have SQL queries."));
        }

        readyHandler.handle(Future.succeededFuture(this));
    }

    @Override
    public HashMap<AccountSqlQuery, String> loadSqlQueries() {
        HashMap<AccountSqlQuery, String> loadedSqlQueries = new HashMap<>();
        Properties queriesProps = new Properties();

        try (InputStream queriesInputStream = getClass().getResourceAsStream("/account-db-queries.properties")) {
            queriesProps.load(queriesInputStream);

            loadedSqlQueries.put(AccountSqlQuery.SELECT_ACCOUNT_BY_USERNAME_OR_EMAIL, queriesProps.getProperty("select-account-by-username-or-email"));
            loadedSqlQueries.put(AccountSqlQuery.INSERT_ACCOUNT_WITH_RETURNING, queriesProps.getProperty("insert-account-with-returning"));
        } catch (IOException e) {
            log.error("Can't load sql queries.", e.getCause());
        }

        return loadedSqlQueries;
    }

    @Override
    public AccountService getAccountByUsernameOrEmail(String UsernameOrEmail, Handler<AsyncResult<Account>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(sqlQueries.get(AccountSqlQuery.SELECT_ACCOUNT_BY_USERNAME_OR_EMAIL))
                        .execute(Tuple.of(UsernameOrEmail), ar2 -> {
                            if (ar2.succeeded()) {
                                RowSet<Row> queryResult = ar2.result();

                                Account response;
                                if (queryResult.rowCount() == 0) {
                                    response = Account.createEmpty();
                                } else {
                                    response = new Account(queryResult.iterator().next());
                                }

                                resultHandler.handle(Future.succeededFuture(response));
                            } else {
                                log.warn("Can't query to database cause " + ar2.cause());
                                resultHandler.handle(Future.failedFuture(ar2.cause()));
                            }
                        });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });

        return this;
    }

    @Override
    public AccountService registerAccount(PreRegistrationAccount preRegistrationAccount, Handler<AsyncResult<Account>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
            SqlConnection connection = ar1.result();

            connection
                    .preparedQuery(sqlQueries.get(AccountSqlQuery.INSERT_ACCOUNT_WITH_RETURNING))
                    .execute(Tuple.of(
                            preRegistrationAccount.username,
                            preRegistrationAccount.email,
                            preRegistrationAccount.password), ar2 -> {

                        Account account;
                        if (ar2.succeeded()) {
                            account = new Account(ar2.result().iterator().next());
                        } else {
                            account = Account.createEmpty();
                            log.warn("Can't query to database cause " + ar2.cause());
                        }

                        resultHandler.handle(Future.succeededFuture(account));
                    });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });

        return this;
    }
}
