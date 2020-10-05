package dizzybrawl.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpServerVerticle extends AbstractVerticle {
    private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";

    private static final Logger log = LoggerFactory.getLogger(HttpServerVerticle.class);

    private String dizzyBrawlDbQueue = "dizzybrawl.db.queue";

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.mountSubRouter(EndPoints.MOUNT_SUB_API, router);

        // handlers
        router.get(EndPoints.GET_HERO_BY_ID).handler(this::getHeroById);

        vertx.createHttpServer()
             .requestHandler(router)
             .listen(config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080));
    }

    private void getHeroById(RoutingContext context) {
        // request to event bus
        String requestedHeroId = context.request().getParam("id");
        JsonObject request = new JsonObject().put("hero", requestedHeroId);

        // options for event bus
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "get-hero-by-id");

        // async handler for received response from event bus
        vertx.eventBus().request(dizzyBrawlDbQueue, request, options, reply -> {
           if (reply.succeeded()) {
               JsonObject responseBody = (JsonObject) reply.result().body();
               context.end(responseBody.encodePrettily());
           } else {
               context.fail(reply.cause());
           }
        });
    }
}
