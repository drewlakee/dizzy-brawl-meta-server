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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:verticles.properties")
public class RestHTTPServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(RestHTTPServerVerticle.class);

    private final Environment environment;

    private final AccountApi accountApi;
    private final CharacterApi characterApi;
    private final TaskApi taskApi;
    private final ServerApi serverApi;

    @Autowired
    public RestHTTPServerVerticle(AccountApi accountApi,
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
                .setPort(environment.getProperty("http.server.port", Integer.class, 8080));

        vertx.createHttpServer(serverOptions).requestHandler(getConfiguredApiRouter()).listen(ar -> {
            if (ar.succeeded()) {
                startPromise.complete();
                log.info(String.format("%s deployment ID: %s", this.getClass().getSimpleName(), this.deploymentID()));
            } else {
                log.error("Could not launch http server: " + ar.cause());
                startPromise.fail(ar.cause());
            }
        });
    }

    private Router getConfiguredApiRouter() {
        Router router = Router.router(vertx);
        router.mountSubRouter(environment.getProperty("http.server.endpoints.prefix", "/api"), router);
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
