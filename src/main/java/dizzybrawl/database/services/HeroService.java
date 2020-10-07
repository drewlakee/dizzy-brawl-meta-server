package dizzybrawl.database.services;

import dizzybrawl.database.services.impls.PgHeroService;
import dizzybrawl.database.sql.HeroSqlQuery;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.SqlClient;

import java.util.HashMap;

@ProxyGen
@VertxGen
public interface HeroService {

    @GenIgnore
    static HeroService create(SqlClient dbClient, HashMap<HeroSqlQuery, String> sqlQueries, Handler<AsyncResult<HeroService>> readyHandler) {
        // TODO: use switch for different kind of service and enum
        return new PgHeroService(dbClient, sqlQueries, readyHandler);
    }

    @GenIgnore
    static HeroService createProxy(Vertx vertx, String eventBusAddress) {
        return new HeroServiceVertxEBProxy(vertx, eventBusAddress);
    }

    @Fluent
    HeroService getHeroById(int heroId, Handler<AsyncResult<JsonObject>> resultHandler);
}
