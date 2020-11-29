package dizzybrawl;

import dizzybrawl.database.PgDatabaseVerticle;
import dizzybrawl.http.RestHTTPServerVerticle;
import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VertxLauncherVerticle extends AbstractVerticle {

    public static String CONFIG_SHARED_WORKERS_SIZE = "vertx.rest.server.workers.size";

    private static final Logger log = LoggerFactory.getLogger(VertxLauncherVerticle.class);

    private final RestHTTPServerVerticle restHTTPServerVerticle;

    @Autowired
    public VertxLauncherVerticle(RestHTTPServerVerticle restHTTPServerVerticle) {
        this.restHTTPServerVerticle = restHTTPServerVerticle;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Verticles deploy process starts.");

        DeploymentOptions restServerOptions = new DeploymentOptions()
                .setWorker(true)
                .setWorkerPoolName("rest-server-worker")
                .setWorkerPoolSize(config().getInteger(CONFIG_SHARED_WORKERS_SIZE, 5));

        CompositeFuture.all(
                deploy(new PgDatabaseVerticle()),
                deploy(restHTTPServerVerticle, restServerOptions)
        ).onComplete(ar1 -> {
            startPromise.complete();
            log.info("Verticles deploy process successfully done.");
        }).onFailure(ar2 -> {
            startPromise.fail(ar2.getCause());
            log.error("Verticles deploy process failed.", ar2.getCause());
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
