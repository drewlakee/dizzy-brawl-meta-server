package dizzybrawl.http;

import dizzybrawl.database.daos.AccountNioDao;
import dizzybrawl.database.services.CharacterService;
import dizzybrawl.database.services.TaskService;
import dizzybrawl.http.api.AccountApi;
import dizzybrawl.http.api.CharacterApi;
import dizzybrawl.http.api.TaskApi;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestHTTPServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(RestHTTPServerVerticle.class);

    private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";


    private final AccountApi accountApi;
    private final AccountNioDao accountNioDao;

    private CharacterService characterService;
    private TaskService taskService;

    @Autowired
    public RestHTTPServerVerticle(AccountApi accountApi, AccountNioDao accountNioDao) {
        this.accountApi = accountApi;
        this.accountNioDao = accountNioDao;
    }

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

        // api end points handlers
        router.post("/account/auth/login")
                .handler(jsonObjectValidationHandler)
                .handler(accountApi.onLogin(accountNioDao));

        router.post("/account/register")
                .handler(jsonObjectValidationHandler)
                .handler(accountApi.onRegistration(accountNioDao));

        router.post("/characters/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(CharacterApi.getAllCharactersByAccountUUID(characterService));

        router.post("/characters/meshes/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(CharacterApi.getAllCharactersMeshesByCharacterUUID(characterService));

        router.post("/tasks/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(TaskApi.getTasksByAccountUUID(taskService));

        router.post("/tasks/add")
                .handler(jsonObjectValidationHandler)
                .handler(TaskApi.addTasks(taskService));

        router.put("/tasks/update/progress")
                .handler(jsonObjectValidationHandler)
                .handler(TaskApi.updateTasksProgress(taskService));

        return router;
    }
}
