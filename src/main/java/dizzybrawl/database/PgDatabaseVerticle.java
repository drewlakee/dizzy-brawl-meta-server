package dizzybrawl.database;

import dizzybrawl.database.services.AccountService;
import dizzybrawl.database.services.CharacterService;
import dizzybrawl.database.services.TaskService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;


public class PgDatabaseVerticle extends AbstractVerticle {

    private static final String CONFIG_PG_HOST = "postgresql.host";
    private static final String CONFIG_PG_PORT = "postgresql.port";
    private static final String CONFIG_PG_DATABASE = "postgresql.database";
    private static final String CONFIG_PG_USERNAME = "postgresql.username";
    private static final String CONFIG_PG_PASSWORD = "postgresql.password";
    private static final String CONFIG_PG_POOL_MAX_SIZE = "postgresql.pool.maxsize";
    public static final String CONFIG_DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";

    private static final Logger log = LoggerFactory.getLogger(PgDatabaseVerticle.class);

    private PgPool pgPool;

    @Override
    public void start(Promise<Void> startPromise) {
        // Configure connection to database
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(config().getString(CONFIG_PG_HOST, "localhost"))
                .setPort(config().getInteger(CONFIG_PG_PORT, 5432))
                .setDatabase(config().getString(CONFIG_PG_DATABASE, "dizzybrawl"))
                .setUser(config().getString(CONFIG_PG_USERNAME, "postgres"))
                .setPassword(config().getString(CONFIG_PG_PASSWORD, "1"));

        // Workers pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(config().getInteger(CONFIG_PG_POOL_MAX_SIZE, 2));

        // Create the client pool
        pgPool = PgPool.pool(vertx, connectOptions, poolOptions);

        // Binding service handling on event bus by address
        AccountService.create(pgPool, ar1 -> {
            if (ar1.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);
                binder
                        .setAddress(CONFIG_DIZZYBRAWL_DB_QUEUE + ".service.account")
                        .register(AccountService.class, ar1.result());
            } else {
                log.error("Service can't be binded.", ar1.cause());
                startPromise.fail(ar1.cause());
            }
        });

        CharacterService.create(pgPool, ar1 -> {
            if (ar1.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);
                binder
                        .setAddress(CONFIG_DIZZYBRAWL_DB_QUEUE + ".service.character")
                        .register(CharacterService.class, ar1.result());
            } else {
                log.error("Service can't be binded.", ar1.cause());
                startPromise.fail(ar1.cause());
            }
        });

        TaskService.create(pgPool, ar1 -> {
           if (ar1.succeeded()) {
               ServiceBinder binder = new ServiceBinder(vertx);
               binder
                       .setAddress(CONFIG_DIZZYBRAWL_DB_QUEUE + ".service.task")
                       .register(TaskService.class, ar1.result());
               startPromise.complete();
           } else {
               log.error("Service can't be binded.", ar1.cause());
               startPromise.fail(ar1.cause());
           }
        });

        // TODO: create default tables creation
    }

    @Override
    public void stop() {
        pgPool.close();
    }
}
