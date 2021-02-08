package dizzybrawl.verticles;

import dizzybrawl.http.api.AccountApi;
import dizzybrawl.http.api.CharacterApi;
import dizzybrawl.http.api.ServerApi;
import dizzybrawl.http.api.TaskApi;
import dizzybrawl.http.validation.JsonObjectValidationHandler;
import dizzybrawl.http.validation.ValidationHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class WebServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(WebServerVerticle.class);

    private final Environment environment;

    private final AccountApi accountApi;
    private final CharacterApi characterApi;
    private final TaskApi taskApi;
    private final ServerApi serverApi;

    @Autowired
    public WebServerVerticle(AccountApi accountApi,
                             CharacterApi characterApi,
                             TaskApi taskApi,
                             ServerApi serverApi,
                             Environment environment) {
        this.accountApi = accountApi;
        this.characterApi = characterApi;
        this.taskApi = taskApi;
        this.serverApi = serverApi;

        this.environment = environment;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServerOptions serverOptions = new HttpServerOptions()
                .setHost(environment.getProperty("vertx.server.ip.v4", "0.0.0.0"))
                .setPort(environment.getProperty("vertx.server.port", Integer.class, 8081));

        vertx.createHttpServer(serverOptions).requestHandler(getConfiguredApiRouter()).listen(ar -> {
            if (ar.succeeded()) {
                startPromise.complete();
                log.info("Web Server launched on " + serverOptions.getHost() + ":" + serverOptions.getPort());
            } else {
                log.error("Could not launch HTTP server cause {}", ar.cause());
                startPromise.fail(ar.cause());
            }
        });
    }

    private Router getConfiguredApiRouter() {
        Router router = Router.router(vertx);
        router.mountSubRouter(environment.getProperty("server.context.path", "/api/v1"), router);

        if (environment.containsProperty("server.context.path")) {
            log.info("Web Server endpoints context path '" + environment.getProperty("server.context.path", "/api/v1") + "'");
        }

        router.route().handler(rh -> {
            rh.response()
                    .setChunked(true)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            rh.next();
        });

        router.route().handler(BodyHandler.create());

        ValidationHandler jsonObjectValidationHandler = JsonObjectValidationHandler.create();

        router.post("/accounts/auth/login")
                .handler(jsonObjectValidationHandler)
                .handler(accountApi.onLogin(vertx));

        router.post("/accounts/register")
                .handler(jsonObjectValidationHandler)
                .handler(accountApi.onRegistration(vertx));

        router.post("/characters/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(characterApi.onGetAllCharacters(vertx));

        router.post("/characters/armors/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(characterApi.onGetAllArmors(vertx));

        router.post("/characters/weapons/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(characterApi.onGetAllWeapons(vertx));

        router.post("/tasks/get/all")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.onGetAll(vertx));

        router.post("/tasks/add")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.onAdd(vertx));

        router.put("/tasks/update/progress")
                .handler(jsonObjectValidationHandler)
                .handler(taskApi.onUpdateProgress(vertx));

        router.post("/servers/add")
                .handler(jsonObjectValidationHandler)
                .handler(serverApi.onAdd(vertx));

        router.post("/servers/get/all")
                .handler(serverApi.onGetAll(vertx));

        router.delete("/servers/delete")
                .handler(jsonObjectValidationHandler)
                .handler(serverApi.onDelete(vertx));

        return router;
    }
}
