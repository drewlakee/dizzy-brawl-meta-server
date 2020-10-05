package dizzybrawl.database.services.impls;

import dizzybrawl.database.services.HeroService;
import dizzybrawl.database.sql.SqlQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.SqlClient;

import java.util.HashMap;

public class PgHeroService implements HeroService {

    public PgHeroService(SqlClient dbClient, HashMap<SqlQuery, String> sqlQueries, Handler<AsyncResult<HeroService>> readyHandler) {

    }

    @Override
    public HeroService getHeroById(int heroId, Handler<AsyncResult<JsonObject>> resultHandler) {
        return null;
    }
}
