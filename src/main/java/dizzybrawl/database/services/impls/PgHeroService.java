package dizzybrawl.database.services.impls;

import dizzybrawl.database.services.HeroService;
import dizzybrawl.database.sql.SqlQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;

import java.util.HashMap;

public class PgHeroService implements HeroService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PgHeroService.class);

    private final HashMap<SqlQuery, String> sqlQueries;
    private final PgPool dbClient;

    public PgHeroService(SqlClient dbClient, HashMap<SqlQuery, String> sqlQueries, Handler<AsyncResult<HeroService>> readyHandler) {
        this.dbClient = (PgPool) dbClient;
        this.sqlQueries = sqlQueries;

        this.dbClient.getConnection(ar -> {
            if (ar.failed()) {
                LOGGER.error("Could not open a database connection", ar.cause());
                readyHandler.handle(Future.failedFuture(ar.cause()));
            } else {
                // TODO: CREATE TABLES
                readyHandler.handle(Future.succeededFuture(this));
            }
        });
    }

    @Override
    public HeroService getHeroById(int heroId, Handler<AsyncResult<JsonObject>> resultHandler) {
        return null;
    }
}
