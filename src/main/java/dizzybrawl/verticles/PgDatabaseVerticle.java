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
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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


        postPostgresInitialization()
                .onComplete(handler -> startPromise.complete())
                .onFailure(startPromise::fail);
    }

    public Future<Void> postPostgresInitialization() {
        String scripts;
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("pg-post-init.sql")) {
            scripts = StreamUtils.copyToString(in, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Post Postgres Initialization error cause ", e.getCause());
            return Future.failedFuture("Post Postgres Initialization error");
        }

        if (scripts.isBlank()) {
            return Future.failedFuture("Post Postgres Initialization file is empty");
        }

        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                Transaction transaction = connection.begin();
                transaction.query(scripts).execute(ar2 -> {
                    if (ar2.succeeded()) {
                        log.info("Post Postgres Initialization was successfully done");
                        transaction.commit();
                    } else {
                        log.error("Post Postgres Initialization transaction error cause ", ar2.cause());
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
