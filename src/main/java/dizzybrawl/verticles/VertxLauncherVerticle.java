package dizzybrawl.verticles;

import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapperMessageCodec;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class VertxLauncherVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VertxLauncherVerticle.class);

    public final Environment environment;

    private final WebServerVerticle webServerVerticle;
    private final AccountServiceVerticle accountServiceVerticle;
    private final CharacterServiceVerticle characterServiceVerticle;
    private final TaskServiceVerticle taskServiceVerticle;
    private final ServerServiceVerticle serverServiceVerticle;
    private final PgDatabaseVerticle pgDatabaseVerticle;

    @Autowired
    public VertxLauncherVerticle(WebServerVerticle webServerVerticle,
                                 PgDatabaseVerticle pgDatabaseVerticle,
                                 AccountServiceVerticle accountServiceVerticle,
                                 CharacterServiceVerticle characterServiceVerticle,
                                 TaskServiceVerticle taskServiceVerticle,
                                 ServerServiceVerticle serverServiceVerticle,
                                 Environment environment) {
        this.webServerVerticle = webServerVerticle;
        this.pgDatabaseVerticle = pgDatabaseVerticle;
        this.accountServiceVerticle = accountServiceVerticle;
        this.characterServiceVerticle = characterServiceVerticle;
        this.taskServiceVerticle = taskServiceVerticle;
        this.serverServiceVerticle = serverServiceVerticle;

        this.environment = environment;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Verticles deploy process starts.");

        // register custom codecs for event bus communication
        vertx.eventBus()
                .registerDefaultCodec(EventBusObjectWrapper.class, new EventBusObjectWrapperMessageCodec());

        DeploymentOptions webServerDeployment = new DeploymentOptions()
                .setWorker(true)
                .setWorkerPoolName("http-server-workers-pool");

        if (environment.containsProperty("server.workers.pool.count")) {
            webServerDeployment.setWorkerPoolSize(environment.getProperty("server.workers.pool.count", Integer.class, 1));
            log.info("Vertx Web-Server workers pool [{}] size: {} threads", webServerDeployment.getWorkerPoolName(), webServerDeployment.getWorkerPoolSize());
        }

        CompositeFuture.all(
            deploy(webServerVerticle, webServerDeployment),
            deploy(accountServiceVerticle),
            deploy(characterServiceVerticle),
            deploy(taskServiceVerticle),
            deploy(serverServiceVerticle),
            deploy(pgDatabaseVerticle)
        ).onSuccess(handler -> {
            startPromise.complete();
            log.info("Verticles deploy process successfully done");
        }).onFailure(handler -> {
            handler.printStackTrace();
            startPromise.fail(handler.getCause());
            log.error("Verticles deploy process failed cause {0}", handler.getCause());
        });
    }

    private Future<Void> deploy(AbstractVerticle verticle, DeploymentOptions deploymentOptions) {
        return Future.future(handler -> vertx.deployVerticle(verticle, deploymentOptions, ar1 -> {
            if (ar1.succeeded()) {
                handler.complete();
            } else {
                ar1.cause().printStackTrace();
                handler.fail(ar1.cause());
            }
        }));
    }

    private Future<Void> deploy(AbstractVerticle verticle) {
        return Future.future(handler -> vertx.deployVerticle(verticle, ar1 -> {
            if (ar1.succeeded()) {
                handler.complete();
            } else {
                ar1.cause().printStackTrace();
                handler.fail(ar1.cause());
            }
        }));
    }
}
