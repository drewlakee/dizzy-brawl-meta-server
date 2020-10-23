package dizzybrawl;

import dizzybrawl.database.PgDatabaseVerticle;
import dizzybrawl.http.RestServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class MainVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Main Verticle starts deploying.");

        vertx.deployVerticle(new PgDatabaseVerticle(), ar1 -> {
            if (ar1.succeeded()) {
                vertx.deployVerticle(new RestServerVerticle(), ar2 -> {
                    if (ar2.succeeded()) {
                        log.error("Main Verticle successfully deployed.");
                        startPromise.complete();
                    } else {
                        log.error("Main Verticle deploying fail.", ar2.cause());
                        startPromise.fail(ar2.cause());
                    }
                });
            } else {
                log.error("Main Verticle deploying fail.", ar1.cause());
                startPromise.fail(ar1.cause());
            }
        });
    }
}
