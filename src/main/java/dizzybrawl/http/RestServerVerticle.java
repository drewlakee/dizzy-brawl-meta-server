package dizzybrawl.http;

import dizzybrawl.database.services.AccountService;
import dizzybrawl.database.services.CharacterService;
import dizzybrawl.database.services.TaskService;
import dizzybrawl.http.api.AccountApi;
import dizzybrawl.http.api.CharacterApi;
import dizzybrawl.http.api.TaskApi;
import dizzybrawl.http.validation.JsonArrayValidationHandler;
import dizzybrawl.http.validation.JsonObjectValidationHandler;
import dizzybrawl.http.validation.ValidationHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class RestServerVerticle extends AbstractVerticle {

    private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";

    private static final Logger log = LoggerFactory.getLogger(RestServerVerticle.class);

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

    private void initializeServices() {
        accountService = AccountService.createProxy(vertx, DIZZYBRAWL_DB_QUEUE + ".service.account");
        characterService = CharacterService.createProxy(vertx, DIZZYBRAWL_DB_QUEUE + ".service.character");
        taskService = TaskService.createProxy(vertx, DIZZYBRAWL_DB_QUEUE + ".service.task");
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
            rh.response()
                    .setChunked(true)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            rh.next();
        });

        router.route().handler(BodyHandler.create());

        // validation handlers
        ValidationHandler jsonObjectValidationHandler = JsonObjectValidationHandler.create();
        ValidationHandler jsonArrayValidationHandler = JsonArrayValidationHandler.create();

        // api end points handlers
        router.post("/account/auth/login")
                .handler(jsonObjectValidationHandler)
                .handler(AccountApi.onLogin(accountService));

        router.post("/account/register")
                .handler(jsonObjectValidationHandler)
                .handler(AccountApi.onRegistration(accountService));

        router.post("/character/all")
                .handler(jsonObjectValidationHandler)
                .handler(CharacterApi.getAllCharactersByAccountUUID(characterService));

        router.get("/character/mesh/all")
                .handler(jsonArrayValidationHandler)
                .handler(CharacterApi.getAllCharacterMeshesByCharacterUUID(characterService));

        router.get("/task/all")
                .handler(jsonObjectValidationHandler)
                .handler(TaskApi.getTasksByAccountUUID(taskService));

        router.post("/task/add")
                .handler(jsonObjectValidationHandler)
                .handler(TaskApi.addTasks(taskService));

        router.put("/task/update/progress")
                .handler(jsonObjectValidationHandler)
                .handler(TaskApi.updateTasksProgress(taskService));

        return router;
    }
}
