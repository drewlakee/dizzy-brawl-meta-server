package dizzybrawl.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
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

        vertx.deployVerticle(restHTTPServerVerticle, restServerOptions, ar -> {
            if (ar.succeeded()) {
                startPromise.complete();
                log.info("Verticles deploy process successfully done.");
            } else {
                startPromise.fail(ar.cause());
                log.error("Verticles deploy process failed.");
            }
        });
    }
}
