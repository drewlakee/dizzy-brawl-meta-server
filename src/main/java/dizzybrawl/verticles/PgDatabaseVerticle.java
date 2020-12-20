package dizzybrawl.verticles;

import dizzybrawl.database.wrappers.query.executors.Executor;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class PgDatabaseVerticle extends AbstractVerticle {

    public static final String ADDRESS = "pg.database";
    public static final String QUERY_ADDRESS = ADDRESS + ".query";

    private static final Logger log = LoggerFactory.getLogger(PgDatabaseVerticle.class);

    private final PgPool pgClient;

    @Autowired
    public PgDatabaseVerticle(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    @Override
    public void start(Promise<Void> startPromise) {

        vertx.eventBus().<EventBusObjectWrapper<Executor>>consumer(QUERY_ADDRESS, handler -> {
            pgClient.getConnection(ar1 -> {
                if (ar1.succeeded()) {
                    SqlConnection connection = ar1.result();

                    Executor queryExecutor = handler.body().get();
                    queryExecutor.execute(connection);
                } else {
                    log.error("Can't connect to database.", ar1.cause());
                    handler.reply(Future.failedFuture(ar1.cause()));
                }
            });
        });

        buildSqlTriggers()
                .onComplete(handler -> startPromise.complete())
                .onFailure(startPromise::fail);
    }

    public Future<Void> buildSqlTriggers() {
        String triggersScripts;
        try {
            triggersScripts = Files.readString(Path.of(PgDatabaseVerticle.class.getResource("/pg-triggers.sql").toURI()));
        } catch (IOException | URISyntaxException e) {
            log.error("SQL triggers can't to be builder cause ", e.getCause());
            return Future.failedFuture("Can't read SQL triggers from URI");
        }

        if (triggersScripts.isBlank()) {
            return Future.failedFuture("SQL triggers file is empty");
        }

        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                Transaction transaction = connection.begin();
                transaction.query(triggersScripts).execute(ar2 -> {
                    if (ar2.succeeded()) {
                        log.info("SQL triggers was successfully built.");
                        transaction.commit();
                    } else {
                        log.error("Can't execute SQL triggers cause ", ar2.cause());
                        transaction.rollback();
                    }

                    connection.close();
                });
            } else {
                log.error("Can't connect to database cause ", ar1.cause());
            }
        });

        return Future.succeededFuture();
    }
}
