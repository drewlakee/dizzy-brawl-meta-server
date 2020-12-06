package dizzybrawl.verticles;

import dizzybrawl.database.daos.AccountNioDao;
import dizzybrawl.database.daos.CharacterNioDao;
import dizzybrawl.database.daos.ServerNioDao;
import dizzybrawl.database.daos.TaskNioDao;
import dizzybrawl.http.api.AccountApi;
import dizzybrawl.http.api.CharacterApi;
import dizzybrawl.http.api.ServerApi;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:verticles.properties")
public class RestHTTPServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(RestHTTPServerVerticle.class);

    private final Environment environment;

    private final AccountApi accountApi;
    private final AccountNioDao accountNioDao;

    private final CharacterApi characterApi;
    private final CharacterNioDao characterNioDao;

    private final TaskApi taskApi;
    private final TaskNioDao taskNioDao;

    private final ServerApi serverApi;
    private final ServerNioDao serverNioDao;

    @Autowired
    public RestHTTPServerVerticle(AccountApi accountApi, AccountNioDao accountNioDao,
                                  CharacterApi characterApi, CharacterNioDao characterNioDao,
                                  TaskApi taskApi, TaskNioDao taskNioDao,
                                  ServerApi serverApi, ServerNioDao serverNioDao,
                                  Environment environment) {
        this.accountApi = accountApi;
        this.accountNioDao = accountNioDao;
        this.characterApi = characterApi;
        this.characterNioDao = characterNioDao;
        this.taskApi = taskApi;
        this.taskNioDao = taskNioDao;
        this.serverApi = serverApi;
        this.serverNioDao = serverNioDao;

        this.environment = environment;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        launchHttpServer(getConfiguredApiRouter(), ar -> {
            if (ar.succeeded()) {
                startPromise.complete();
                log.info(String.format("%s deployment ID: %s", this.getClass().getSimpleName(), this.deploymentID()));
            } else {
                log.error("Could not launch http server: " + ar.cause());
                startPromise.fail(ar.cause());
            }
        });
    }

    private void launchHttpServer(Router apiRouter, Handler<AsyncResult<HttpServer>> listenHandler) {
        vertx.createHttpServer()
                .requestHandler(apiRouter)
                .listen(environment.getProperty("http.server.port", Integer.class), listenHandler);
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
                .handler(accountApi.onLoginHandler(accountNioDao));

        router.post("/account/register")
                .handler(jsonObjectValidationHandler)
                .handler(accountApi.onRegistrationHandler(accountNioDao));

        router.post("/characters/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(characterApi.getAllCharactersByAccountUUIDHandler(characterNioDao));

        router.post("/characters/armors/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(characterApi.getAllArmorsByAccountsUUIDsHandler(characterNioDao));

        router.post("/tasks/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.getTasksByAccountUUIDHandler(taskNioDao));

        router.post("/tasks/add")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.addTasksHandler(taskNioDao));

        router.put("/tasks/update/progress")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.updateTasksProgressHandler(taskNioDao));

        router.post("/servers/add")
                .handler(jsonObjectValidationHandler)
                .handler(serverApi.addHandler(serverNioDao));

        router.post("/servers/get/all")
                .handler(serverApi.getAllHandler(serverNioDao));

        router.delete("/servers/delete")
                .handler(jsonObjectValidationHandler)
                .handler(serverApi.deleteHandler(serverNioDao));

        return router;
    }
}
