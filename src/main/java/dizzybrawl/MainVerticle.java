package dizzybrawl;

import dizzybrawl.http.HttpServerVerticle;
import dizzybrawl.database.DatabaseVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class MainVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Main Verticle starts deploying.");

        Promise<String> pgDatabaseVerticleDeployment = Promise.promise();
        vertx.deployVerticle(new DatabaseVerticle(), pgDatabaseVerticleDeployment);

        pgDatabaseVerticleDeployment.future().compose(asyncResult -> {
            Promise<String> httpVerticleDeployment = Promise.promise();
            vertx.deployVerticle(new HttpServerVerticle(), httpVerticleDeployment);

            return httpVerticleDeployment.future();
        }).onComplete(asyncResult -> {
            startPromise.complete();
        }).onFailure(asyncResult -> {
            log.error("Main Verticle deployment failed.");
            startPromise.fail(asyncResult.getCause());
        });
    }
}
