package dizzybrawl.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class PgDatabaseVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(PgDatabaseVerticle.class);

    private Future<Void> prepareDatabase() {
        log.info("Prepare database process starts.");

        Promise<Void> promise = Promise.promise();

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("dizzybrawl")
                .setUser("postgres")
                .setPassword("1");

        // Workers pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(30);

        // Create the client pool
        PgPool client = PgPool.pool(vertx, connectOptions, poolOptions);

        // A simple query
        client.query("INSERT INTO hero VALUES ('Sir', 2)")
                .execute(ar -> {
                    if (!ar.succeeded()) {
                        log.error("Prepare database process failed.");
                    }

                    // Worker comes back to pool
                    client.close();
                });

        return promise.future();
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        startPromise.complete(prepareDatabase().result());
    }
}
