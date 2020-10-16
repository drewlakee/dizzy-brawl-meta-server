package dizzybrawl.http;

import dizzybrawl.database.services.AccountService;
import dizzybrawl.database.services.CharacterService;
import dizzybrawl.database.services.TaskService;
import dizzybrawl.http.api.AccountApi;
import dizzybrawl.http.api.CharacterApi;
import dizzybrawl.http.api.TaskApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpDatabaseServerVerticle extends AbstractVerticle {
    private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";

    private static final Logger log = LoggerFactory.getLogger(HttpDatabaseServerVerticle.class);

    private AccountService accountService;
    private CharacterService characterService;
    private TaskService taskService;

    @Override
    public void start(Promise<Void> startPromise) {
        initializeServices();

        launchHttpServer(getConfiguredApiRouter(), listenHandler -> {
            if (listenHandler.succeeded()) {
                startPromise.complete();
            } else {
                log.error("Could not launch http server " + listenHandler.cause());
                startPromise.fail(listenHandler.cause());
            }
        });
    }

    private void launchHttpServer(Router apiRouter, Handler<AsyncResult<HttpServer>> listenHandler) {
        vertx.createHttpServer()
             .requestHandler(apiRouter)
             .listen(config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080), listenHandler);
    }

    private Router getConfiguredApiRouter() {
        Router router = Router.router(vertx);
        router.mountSubRouter("/api/v1", router);
        router.route().handler(rh -> {
            rh.response().putHeader("content-type", "application/json");
            rh.next();
        });

        router.route().handler(BodyHandler.create());

        // api end points handlers
        router.post("/auth/login").handler(AccountApi.onLogin(accountService));
        router.post("/account/register").handler(AccountApi.onRegistration(accountService));

        router.get("/character/all").handler(CharacterApi.getAllCharactersByAccountUUID(characterService));

        router.get("/task/all").handler(TaskApi.getTasksByAccountUUIDWithIntervalInMinutes(taskService));
        router.post("/task/add").handler(TaskApi.addTasks(taskService));

        return router;
    }

    private void initializeServices() {
        // Create async services
        String dbQueueAddress = config().getString(CONFIG_DIZZYBRAWL_DB_QUEUE, "dizzybrawl.db.queue");
        accountService = AccountService.createProxy(vertx, dbQueueAddress + ".service.account");
        characterService = CharacterService.createProxy(vertx, dbQueueAddress + ".service.character");
        taskService = TaskService.createProxy(vertx, dbQueueAddress + ".service.task");
    }
}
