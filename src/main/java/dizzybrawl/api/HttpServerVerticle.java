package dizzybrawl.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpServerVerticle extends AbstractVerticle {
    private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";

    private static final Logger log = LoggerFactory.getLogger(HttpServerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = buildRouter();
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080, "localhost", asyncResult -> {
                    if (asyncResult.succeeded()) {
                        log.info("HTTP server running on port " + 8080);
                        startPromise.complete();
                    } else {
                        log.error("Could not start a HTTP server.", asyncResult.cause());
                        startPromise.fail(asyncResult.cause());
                    }
                });
    }

    private Router buildRouter() {
        Router router = Router.router(vertx);
        router.mountSubRouter(EndPoints.MOUNT_SUB_API, router);

        router.get(EndPoints.GET_HERO_BY_ID).handler(this::getHero);

        return router;
    }

    private void getHero(RoutingContext context) {
        String id = context.request().getParam("id");

        context
                .request()
                .response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(id));
    }
}
