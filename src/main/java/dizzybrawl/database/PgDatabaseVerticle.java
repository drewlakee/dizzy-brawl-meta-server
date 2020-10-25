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
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class PgDatabaseVerticle extends AbstractVerticle {

    private static final String CONFIG_PG_HOST = "postgresql.host";
    private static final String CONFIG_PG_PORT = "postgresql.port";
    private static final String CONFIG_PG_DATABASE = "postgresql.database";
    private static final String CONFIG_PG_USERNAME = "postgresql.username";
    private static final String CONFIG_PG_PASSWORD = "postgresql.password";
    private static final String CONFIG_PG_POOL_MAX_SIZE = "postgresql.pool.maxsize";
    private static final String CONFIG_PG_DB_SCRIPTS_EXECUTE = "postgresql.sql.upload";

    public static final String CONFIG_DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";

    private static final Logger log = LoggerFactory.getLogger(PgDatabaseVerticle.class);

    private PgPool clientPgPool;

    @Override
    public void start(Promise<Void> startPromise) {
        configureDatabaseConnectionOptions();

        // returns true if successfully executed or if doesn't need to execute scripts
        if (executeDatabaseScriptsIfNeeded(startPromise, config().getBoolean(CONFIG_PG_DB_SCRIPTS_EXECUTE, false))) {
            createDatabaseServices(startPromise);
        }

        if (!startPromise.tryComplete()) {
            startPromise.complete();
        }
    }

    private void configureDatabaseConnectionOptions() {
        PgConnectOptions connectionOptions = new PgConnectOptions()
                .setHost(config().getString(CONFIG_PG_HOST, "localhost"))
                .setPort(config().getInteger(CONFIG_PG_PORT, 5432))
                .setDatabase(config().getString(CONFIG_PG_DATABASE, "dizzybrawl"))
                .setUser(config().getString(CONFIG_PG_USERNAME, "postgres"))
                .setPassword(config().getString(CONFIG_PG_PASSWORD, "1"));

        PoolOptions connectionPoolOptions = new PoolOptions()
                .setMaxSize(config().getInteger(CONFIG_PG_POOL_MAX_SIZE, 5));

        clientPgPool = PgPool.pool(vertx, connectionOptions, connectionPoolOptions);
    }

    private void createDatabaseServices(Promise<Void> startPromise) {
        AccountService.create(clientPgPool, ar1 -> {
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

        CharacterService.create(clientPgPool, ar1 -> {
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

        TaskService.create(clientPgPool, ar1 -> {
           if (ar1.succeeded()) {
               ServiceBinder binder = new ServiceBinder(vertx);
               binder
                       .setAddress(CONFIG_DIZZYBRAWL_DB_QUEUE + ".service.task")
                       .register(TaskService.class, ar1.result());
           } else {
               log.error("Service can't be binded.", ar1.cause());
               startPromise.fail(ar1.cause());
           }
        });
    }

    private boolean executeDatabaseScriptsIfNeeded(Promise<Void> startPromise, boolean isNeeded) {
        AtomicBoolean isSuccessfullyExecuted = new AtomicBoolean(true);

        if (isNeeded) {
            clientPgPool.getConnection(ar1 -> {
                if (ar1.succeeded()) {
                    SqlConnection connection = ar1.result();

                    Transaction transaction = connection.begin();

                    String dbSqlScripts = "";
                    try {
                        dbSqlScripts = Files.readString(Path.of(getClass().getResource("/pg-db-dizzy-brawl.sql").toURI()));
                    } catch (IOException | URISyntaxException e) {
                        log.error("Database's scripts can't be loaded from sql file.", e.getCause());
                    }

                    if (dbSqlScripts.isEmpty()) {
                        transaction.rollback();
                        connection.close();
                        isSuccessfullyExecuted.set(false);
                        return;
                    }

                    transaction
                            .query(dbSqlScripts)
                            .execute(ar2 -> {
                                if (ar2.succeeded()) {
                                    transaction.commit();
                                    connection.close();
                                } else {
                                    log.error("SQL Script can't be executed.", ar2.cause());
                                }
                            });
                } else {
                    log.error("Can't connect to database.", ar1.cause());
                    startPromise.fail(ar1.cause());
                }
            });
        }

        return isSuccessfullyExecuted.get();
    }

    @Override
    public void stop() {
        clientPgPool.close();
    }
}
