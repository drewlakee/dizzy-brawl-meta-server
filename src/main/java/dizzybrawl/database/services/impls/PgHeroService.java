package dizzybrawl.database.services.impls;

import dizzybrawl.database.services.HeroService;
import dizzybrawl.database.sql.HeroSqlQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.util.HashMap;

public class PgHeroService implements HeroService {

    private static final Logger log = LoggerFactory.getLogger(PgHeroService.class);

    private final HashMap<HeroSqlQuery, String> sqlQueries;
    private final PgPool dbClient;

    public PgHeroService(SqlClient dbClient, HashMap<HeroSqlQuery, String> sqlQueries, Handler<AsyncResult<HeroService>> readyHandler) {
        // TODO: check cast exception
        this.dbClient = (PgPool) dbClient;
        this.sqlQueries = sqlQueries;
        readyHandler.handle(Future.succeededFuture(this));
    }

    @Override
    public HeroService getHeroById(int heroId, Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.getConnection(ar1 -> {
            SqlConnection connection = ar1.result();
            JsonObject response = new JsonObject();

            connection
                    .preparedQuery(sqlQueries.get(HeroSqlQuery.GET_HERO_BY_ID))
                    .execute(Tuple.of(heroId), ar2 -> {
                        if (ar1.succeeded()) {
                            RowSet<Row> queryResult = ar2.result();

                            if (queryResult.rowCount() == 0) {
                                response.put("found", false);
                            } else {
                                Row queriedHero = queryResult.iterator().next();
                                response.put("hero_id", queriedHero.getInteger("hero_id"));
                                response.put("name", queriedHero.getString("name"));
                                response.put("level", queriedHero.getInteger("level"));
                                response.put("found", true);
                            }

                            resultHandler.handle(Future.succeededFuture(response));
                        } else {
                            log.error("Database query error " + ar2.cause());
                            resultHandler.handle(Future.failedFuture(ar2.cause()));
                        }
                    });
        });

        return this;
    }
}
