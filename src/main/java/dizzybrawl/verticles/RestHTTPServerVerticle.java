package dizzybrawl.verticles;

import dizzybrawl.database.daos.AccountNioDao;
import dizzybrawl.database.daos.CharacterNioDao;
import dizzybrawl.database.daos.TaskNioDao;
import dizzybrawl.http.api.AccountApi;
import dizzybrawl.http.api.CharacterApi;
import dizzybrawl.http.api.TaskApi;
import dizzybrawl.http.validation.JsonObjectValidationHandler;
import dizzybrawl.http.validation.ValidationHandler;
import io.vertx.core.*;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.net.impl.SocketAddressImpl;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RestHTTPServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(RestHTTPServerVerticle.class);

    private static int verticleInstanceCounter = 0;
    private final int serverNumber = verticleInstanceCounter++;

    private final AccountApi accountApi;
    private final AccountNioDao accountNioDao;

    private final CharacterApi characterApi;
    private final CharacterNioDao characterNioDao;

    private final TaskApi taskApi;
    private final TaskNioDao taskNioDao;

    @Autowired
    public RestHTTPServerVerticle(AccountApi accountApi, AccountNioDao accountNioDao,
                                  CharacterApi characterApi, CharacterNioDao characterNioDao,
                                  TaskApi taskApi, TaskNioDao taskNioDao) {
        this.accountApi = accountApi;
        this.accountNioDao = accountNioDao;
        this.characterApi = characterApi;
        this.characterNioDao = characterNioDao;
        this.taskApi = taskApi;
        this.taskNioDao = taskNioDao;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        launchHttpServer(getConfiguredApiRouter(), ar -> {
            if (ar.succeeded()) {
                startPromise.complete();
                log.info("Rest-HTTP-Server-" + serverNumber + " launched on port-" + ar.result().actualPort());
            } else {
                log.error("Could not launch http server: " + ar.cause());
                startPromise.fail(ar.cause());
            }
        });
    }

    private void launchHttpServer(Router apiRouter, Handler<AsyncResult<HttpServer>> listenHandler) {
        int currentPort = 8080;
        int toPort = 65535;

        InetSocketAddress address = new InetSocketAddress(currentPort);
        while (address.isUnresolved() && currentPort <= toPort) {
            address = new InetSocketAddress(++currentPort);
        }

        if (currentPort <= toPort) {
            SocketAddress socket = new SocketAddressImpl(address.getPort(), address.getHostName());
            vertx.createHttpServer()
                    .requestHandler(apiRouter)
                    .listen(socket, listenHandler);
        } else {
            listenHandler.handle(Future.failedFuture("Rest-HTTP-Server-" + serverNumber + " doesn't find free port."));
        }
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

        router.post("/characters/meshes/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(characterApi.getAllCharactersMeshesByCharacterUUIDHandler(characterNioDao));

        router.post("/tasks/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.getTasksByAccountUUIDHandler(taskNioDao));

        router.post("/tasks/add")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.addTasksHandler(taskNioDao));

        router.put("/tasks/update/progress")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.updateTasksProgressHandler(taskNioDao));

        return router;
    }
}
