package dizzybrawl.verticles;

import dizzybrawl.database.models.*;
import dizzybrawl.database.models.Character;
import dizzybrawl.database.wrappers.query.executors.AsyncQueryExecutor;
import dizzybrawl.database.wrappers.query.executors.BatchAtomicAsyncQueryExecutor;
import dizzybrawl.database.wrappers.query.executors.TupleAsyncQueryExecutor;
import dizzybrawl.verticles.eventBus.codecs.*;
import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
@PropertySource(value = "classpath:http.properties")
public class VertxLauncherVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VertxLauncherVerticle.class);

    public final Environment environment;

    private final RestHTTPServerVerticle restHTTPServerVerticle;
    private final AccountServiceVerticle accountServiceVerticle;
    private final CharacterServiceVerticle characterServiceVerticle;
    private final TaskServiceVerticle taskServiceVerticle;
    private final ServerServiceVerticle serverServiceVerticle;
    private final PgDatabaseVerticle pgDatabaseVerticle;

    @Autowired
    public VertxLauncherVerticle(RestHTTPServerVerticle restHTTPServerVerticle,
                                 PgDatabaseVerticle pgDatabaseVerticle,
                                 AccountServiceVerticle accountServiceVerticle,
                                 CharacterServiceVerticle characterServiceVerticle,
                                 TaskServiceVerticle taskServiceVerticle,
                                 ServerServiceVerticle serverServiceVerticle,
                                 Environment environment) {
        this.restHTTPServerVerticle = restHTTPServerVerticle;
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
                .registerDefaultCodec(Account.class, new AccountMessageCodec())
                .registerDefaultCodec(Character.class, new CharacterMessageCodec())
                .registerDefaultCodec(UUID.class, new UUIDMessageCodec())
                .registerDefaultCodec(ArrayList.class, new ArrayListMessageCodec())
                .registerDefaultCodec(Armor.class, new ArmorMessageCodec())
                .registerDefaultCodec(Task.class, new TaskMessageCodec())
                .registerDefaultCodec(Server.class, new ServerMessageCodec())
                .registerDefaultCodec(TupleAsyncQueryExecutor.class, new TupleAsyncQueryExecutorMessageCodec())
                .registerDefaultCodec(BatchAtomicAsyncQueryExecutor.class, new BatchAtomicAsyncQueryExecutorMessageCodec())
                .registerDefaultCodec(AsyncQueryExecutor.class, new AsyncQueryExecutorMessageCodec());

        DeploymentOptions restServerDeploymentOptions = new DeploymentOptions()
                .setWorker(true)
                .setWorkerPoolName("rest-server-worker")
                .setWorkerPoolSize(environment.getProperty("http.rest-workers.pool", Integer.class, 0));

        CompositeFuture.all(
            deploy(restHTTPServerVerticle, restServerDeploymentOptions),
            deploy(accountServiceVerticle),
            deploy(characterServiceVerticle),
            deploy(taskServiceVerticle),
            deploy(serverServiceVerticle),
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
