package dizzybrawl.database;

import dizzybrawl.database.services.HeroService;
import dizzybrawl.database.sql.HeroSqlQuery;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;


public class DatabaseVerticle extends AbstractVerticle {
    private static final String CONFIG_PG_HOST = "postgresql.host";
    private static final String CONFIG_PG_PORT = "postgresql.port";
    private static final String CONFIG_PG_DATABASE = "postgresql.database";
    private static final String CONFIG_PG_USERNAME = "postgresql.username";
    private static final String CONFIG_PG_PASSWORD = "postgresql.password";
    private static final String CONFIG_PG_POOL_MAX_SIZE = "postgresql.pool.maxsize";
    public static final String CONFIG_DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";

    private static final Logger log = LoggerFactory.getLogger(DatabaseVerticle.class);

    private PgPool pgPool;

    private final HashMap<HeroSqlQuery, String> sqlQueries = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) throws IOException {
        // Block Loop Event, but start() calls only at application launch
        loadSqlQueries();

        // Configure connection to database
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(config().getString(CONFIG_PG_HOST, "localhost"))
                .setPort(config().getInteger(CONFIG_PG_PORT, 5432))
                .setDatabase(config().getString(CONFIG_PG_DATABASE, "dizzybrawl"))
                .setUser(config().getString(CONFIG_PG_USERNAME, "postgres"))
                .setPassword(config().getString(CONFIG_PG_PASSWORD, "1"));

        // Workers pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(config().getInteger(CONFIG_PG_POOL_MAX_SIZE, 30));

        // Create the client pool
        pgPool = PgPool.pool(vertx, connectOptions, poolOptions);

        // Bind hero service
        HeroService.create(pgPool, sqlQueries, ar1 -> {
           if (ar1.succeeded()) {
               ServiceBinder binder = new ServiceBinder(vertx);
               binder
                       .setAddress(CONFIG_DIZZYBRAWL_DB_QUEUE)
                       .register(HeroService.class, ar1.result());
               startPromise.complete();
           } else {
               log.error("Hero service can't be binded.", ar1.cause());
               startPromise.fail(ar1.cause());
           }
        });

        // TODO: create default tables creation
    }

    private void loadSqlQueries() throws IOException {
        InputStream queriesInputStream = getClass().getResourceAsStream("/hero-db-queries.properties");

        Properties queriesProps = new Properties();
        queriesProps.load(queriesInputStream);
        queriesInputStream.close();

        sqlQueries.put(HeroSqlQuery.GET_HERO_BY_ID, queriesProps.getProperty("get-hero-by-id"));
    }

    @Override
    public void stop() {
        pgPool.close();
    }
}
