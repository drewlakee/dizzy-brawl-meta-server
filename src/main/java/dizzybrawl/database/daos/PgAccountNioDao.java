package dizzybrawl.database.daos;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.models.VerifiedAccount;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
@PropertySource("classpath:queries/account-db-queries.properties")
public class PgAccountNioDao implements AccountNioDao {

    private static final Logger log = LoggerFactory.getLogger(PgAccountNioDao.class);

    private final Environment environment;

    private final PgPool pgClient;

    @Autowired
    public PgAccountNioDao(PgPool pgPool, Environment environment) {
        this.pgClient = pgPool;
        this.environment = environment;
    }

    @Override
    public void getByUsernameOrEmail(String usernameOrEmail, Handler<AsyncResult<VerifiedAccount>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(environment.getProperty("select-account-by-username-or-email"))
                        .execute(Tuple.of(usernameOrEmail), ar2 -> {
                            if (ar2.succeeded()) {
                                RowSet<Row> queryResult = ar2.result();

                                VerifiedAccount response;
                                if (queryResult.rowCount() == 0) {
                                    response = VerifiedAccount.createEmpty();
                                } else {
                                    response = new VerifiedAccount(queryResult.iterator().next());
                                }

                                resultHandler.handle(Future.succeededFuture(response));
                            } else {
                                log.warn("Can't query to database cause " + ar2.cause());
                                resultHandler.handle(Future.failedFuture(ar2.cause()));
                            }

                            connection.close();
                        });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });
    }

    @Override
    public void register(Account preRegistrationAccount, Handler<AsyncResult<VerifiedAccount>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(environment.getProperty("insert-account-with-returning"))
                        .execute(Tuple.of(
                                preRegistrationAccount.getUsername(),
                                preRegistrationAccount.getEmail(),
                                preRegistrationAccount.getPassword()), ar2 -> {

                            VerifiedAccount verifiedAccount;
                            if (ar2.succeeded()) {
                                verifiedAccount = new VerifiedAccount(ar2.result().iterator().next());
                            } else {
                                verifiedAccount = VerifiedAccount.createEmpty();
                                log.warn("Can't query to database cause " + ar2.cause());
                            }

                            resultHandler.handle(Future.succeededFuture(verifiedAccount));

                            connection.close();
                        });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });
    }
}
