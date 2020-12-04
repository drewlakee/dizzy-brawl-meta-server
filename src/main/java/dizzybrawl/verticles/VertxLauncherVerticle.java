package dizzybrawl.verticles;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:verticles.properties")
public class VertxLauncherVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VertxLauncherVerticle.class);

    public final Environment environment;

    private final RestHTTPServerVerticle restHTTPServerVerticle;

    private final PgDatabaseVerticle pgDatabaseVerticle;

    @Autowired
    public VertxLauncherVerticle(RestHTTPServerVerticle restHTTPServerVerticle,
                                 PgDatabaseVerticle pgDatabaseVerticle,
                                 Environment environment) {
        this.restHTTPServerVerticle = restHTTPServerVerticle;
        this.pgDatabaseVerticle = pgDatabaseVerticle;
        this.environment = environment;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Verticles deploy process starts.");

        DeploymentOptions restServerOptions = new DeploymentOptions()
                .setWorker(true)
                .setWorkerPoolName("rest-server-worker")
                .setWorkerPoolSize(environment.getProperty("http.rest-workers.pool", Integer.class, 0));

        CompositeFuture.all(
            deploy(restHTTPServerVerticle, restServerOptions),
            deploy(pgDatabaseVerticle)
        ).onComplete(handler -> {
            startPromise.complete();
            log.info("Verticles deploy process successfully done.");
        }).onFailure(handler -> {
            handler.printStackTrace();
            startPromise.fail(handler.getCause());
            log.error("Verticles deploy process failed cause ", handler.getCause());
        });
    }

    private Future<Void> deploy(AbstractVerticle verticle, DeploymentOptions deploymentOptions) {
        return Future.future(handler -> {
            vertx.deployVerticle(verticle, deploymentOptions, ar1 -> {
                if (ar1.succeeded()) {
                    handler.complete();
                } else {
                    ar1.cause().printStackTrace();
                    handler.fail(ar1.cause());
                }
            });
        });
    }

    private Future<Void> deploy(AbstractVerticle verticle) {
        return Future.future(handler -> {
            vertx.deployVerticle(verticle, ar1 -> {
                if (ar1.succeeded()) {
                    handler.complete();
                } else {
                    ar1.cause().printStackTrace();
                    handler.fail(ar1.cause());
                }
            });
        });
    }
}
