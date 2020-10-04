package dizzybrawl.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;


public class DatabaseVerticle extends AbstractVerticle {
    private static final String CONFIG_PG_HOST = "postgresql.host";
    private static final String CONFIG_PG_PORT = "postgresql.port";
    private static final String CONFIG_PG_DATABASE = "postgresql.database";
    private static final String CONFIG_PG_USERNAME = "postgresql.username";
    private static final String CONFIG_PG_PASSWORD = "postgresql.password";
    private static final String CONFIG_PG_POOL_MAX_SIZE = "postgresql.pool.maxsize";

    private static final Logger log = LoggerFactory.getLogger(DatabaseVerticle.class);

    private PgPool pgPool;

    @Override
    public void start(Promise<Void> startPromise) {
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(config().getString(CONFIG_PG_HOST, "localhost"))
                .setPort(config().getInteger(CONFIG_PG_PORT, 5432))
                .setDatabase(config().getString(CONFIG_PG_DATABASE, "dizzybrawl"))
                .setUser(config().getString(CONFIG_PG_USERNAME, "postgres"))
                .setPassword(config().getString(CONFIG_PG_PASSWORD, "1"));

        // Workers pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(config().getInteger(CONFIG_PG_POOL_MAX_SIZE, 20));

        // Create the client pool
        pgPool = PgPool.pool(vertx, connectOptions, poolOptions);

        // TODO: Insert promise to services init
        startPromise.complete();
    }

    @Override
    public void stop() {
        pgPool.close();
    }
}
