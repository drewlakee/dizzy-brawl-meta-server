package dizzybrawl.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(HttpServerVerticle.class);

    private Future<Void> prepareHttpServer() {
        log.info("Prepare Http Server process starts.");

        Promise<Void> promise = Promise.promise();

        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);

        httpServer
                .requestHandler(router)
                .listen(8080, asyncResult -> {
                   if (asyncResult.succeeded()) {
                       log.info("HTTP server running on port " + 8080);
                       //promise.complete();
                       promise.fail("прошфыв");
                   } else {
                       log.error("Could not start a HTTP server", asyncResult.cause());
                       promise.fail(asyncResult.cause());
                   }
                });


        return promise.future();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        startPromise.complete(prepareHttpServer().result());
    }
}
