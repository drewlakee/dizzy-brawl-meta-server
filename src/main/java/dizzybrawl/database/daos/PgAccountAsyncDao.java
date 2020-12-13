package dizzybrawl.database.daos;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.wrappers.query.executors.TupleAsyncQueryExecutor;
import dizzybrawl.verticles.PgDatabaseVerticle;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@PropertySource("classpath:queries/account-db-queries.properties")
public class PgAccountAsyncDao implements AccountAsyncDao {

    private static final Logger log = LoggerFactory.getLogger(PgAccountAsyncDao.class);

    private final Environment environment;

    @Autowired
    public PgAccountAsyncDao(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void getByUsernameOrEmail(Vertx vertx, String usernameOrEmail, Handler<AsyncResult<Account>> resultHandler) {
        TupleAsyncQueryExecutor queryExecutor = new TupleAsyncQueryExecutor(environment.getProperty("select-account-by-username-or-email"), Tuple.of(usernameOrEmail));
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                RowSet<Row> queryResult = ar1.result();

                if (queryResult.rowCount() == 0) {
                    resultHandler.handle(Future.succeededFuture(Account.createEmpty()));
                } else {
                    resultHandler.handle(Future.succeededFuture(new Account(queryResult.iterator().next())));
                }
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }

    @Override
    public void register(Vertx vertx, Account preRegistrationAccount, Handler<AsyncResult<Account>> resultHandler) {
        UUID generatedUUID = UUID.nameUUIDFromBytes(preRegistrationAccount.getUsername().getBytes());
        Tuple tuple = Tuple.of(
                generatedUUID,
                preRegistrationAccount.getUsername(),
                preRegistrationAccount.getEmail(),
                preRegistrationAccount.getPassword()
        );

        TupleAsyncQueryExecutor queryExecutor = new TupleAsyncQueryExecutor(environment.getProperty("insert-new-account"), tuple);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                preRegistrationAccount.setAccountUUID(generatedUUID);
                resultHandler.handle(Future.succeededFuture(preRegistrationAccount));
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.succeededFuture(Account.createEmpty()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }
}
