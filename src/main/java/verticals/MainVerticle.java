package verticals;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;

public class MainVerticle extends AbstractVerticle {

    private JDBCClient dbClient;

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    private Future<Void> prepareDatabase() {
        Promise<Void> promise = Promise.promise();
        // (...)
        return promise.future();
    }

    private Future<Void> startHttpServer() {
        Promise<Void> promise = Promise.promise();
        // (...)
        return promise.future();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Future<Void> steps = prepareDatabase().compose(v -> startHttpServer());
        startPromise.complete(steps.result());
    }
}
