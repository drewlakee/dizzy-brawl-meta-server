package dizzybrawl;

import dizzybrawl.database.PgDatabaseVerticle;
import dizzybrawl.http.RestServerVerticle;
import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class MainVerticle extends AbstractVerticle {

    public static String CONFIG_SHARED_WORKERS_SIZE = "vertx.rest.server.workers.size";

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Main Verticle starts deploying.");

        DeploymentOptions restServerOptions = new DeploymentOptions()
                .setWorker(true)
                .setWorkerPoolName("rest-server-workers-pool")
                .setWorkerPoolSize(config().getInteger(CONFIG_SHARED_WORKERS_SIZE, 5));

        CompositeFuture.all(
                deploy(new PgDatabaseVerticle()),
                deploy(new RestServerVerticle(), restServerOptions)
        ).onComplete(ar1 -> {
            log.info("Main Verticle successfully deployed.");
            startPromise.complete();
        }).onFailure(ar2 -> {
            log.error("Main Verticle deploying fail.", ar2.getCause());
            startPromise.fail(ar2.getCause());
        });
    }

    private Future<Verticle> deploy(Verticle verticle) {
        return Future.future(promise -> vertx.deployVerticle(verticle, ar -> {
            if (ar.succeeded()) {
                promise.complete(verticle);
            } else {
                promise.fail(ar.cause());
            }
        }));
    }

    private Future<Verticle> deploy(Verticle verticle, DeploymentOptions options) {
        return Future.future(promise -> vertx.deployVerticle(verticle, options, ar -> {
            if (ar.succeeded()) {
                promise.complete(verticle);
            } else {
                promise.fail(ar.cause());
            }
        }));
    }
}
