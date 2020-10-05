package dizzybrawl.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

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

    // Event bus address, that verticle will listen
    public static final String CONFIG_DIZZYBRAWL_DB_QUEUE = "dizzybrawl.db.queue";

    private static final Logger log = LoggerFactory.getLogger(DatabaseVerticle.class);

    private PgPool pgPool;

    private final HashMap<SqlQuery, String> sqlQueries = new HashMap<>();

    private enum SqlQuery {
        GET_HERO_BY_ID
    }

    public enum ErrorCodes {
        NO_ACTION_SPECIFIED,
        BAD_ACTION,
        DB_ERROR
    }

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

        // TODO: Add tables creation
        pgPool.getConnection(asyncResult -> {
            if (asyncResult.succeeded()) {
                vertx.eventBus().consumer(CONFIG_DIZZYBRAWL_DB_QUEUE, this::onMessageReceive);
                startPromise.complete();
            } else {
                startPromise.fail(asyncResult.cause());
            }
        });
    }

    private void loadSqlQueries() throws IOException {
        InputStream queriesInputStream = getClass().getResourceAsStream("/db-queries.properties");

        Properties queriesProps = new Properties();
        queriesProps.load(queriesInputStream);
        queriesInputStream.close();

        sqlQueries.put(SqlQuery.GET_HERO_BY_ID, queriesProps.getProperty("get-hero-by-id"));
    }

    public void onMessageReceive(Message<JsonObject> message) {
        if (!message.headers().contains("action")) {
            message.fail(ErrorCodes.NO_ACTION_SPECIFIED.ordinal(), "No action header specified");
            return;
        }

        String action = message.headers().get("action");

        switch (action) {
            case "get-hero-by-id":
                getHeroById(message);
                break;
            default:
                message.fail(ErrorCodes.BAD_ACTION.ordinal(), "Bad action: " + action);
        }
    }

    private void getHeroById(Message<JsonObject> message) {
        // body of request on event bus
        Integer requestedHeroId = Integer.parseInt(message.body().getString("hero"));
        JsonObject response = new JsonObject();

        pgPool.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();
                connection
                        .preparedQuery(sqlQueries.get(SqlQuery.GET_HERO_BY_ID))
                        .execute(Tuple.of(requestedHeroId), ar2 -> {
                            RowSet<Row> resultQuery = ar2.result();

                            if (resultQuery.iterator().hasNext()) {
                                Row heroRow = resultQuery.iterator().next();
                                response.put("hero_id", heroRow.getInteger("hero_id"));
                                response.put("name", heroRow.getString("name"));
                                response.put("level", heroRow.getInteger("level"));
                            } else {
                                response.put("message", "not found");
                            }

                            message.reply(response);
                        });
            } else {
                log.error("Could not connect: " + ar1.cause().getMessage());
                response.put("message", "could not connect " + ar1.cause().getMessage());
            }
        });
    }

    @Override
    public void stop() {
        pgPool.close();
    }
}
