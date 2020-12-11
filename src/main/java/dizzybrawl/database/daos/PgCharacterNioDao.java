package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import dizzybrawl.database.wrappers.query.executors.TupleAsyncQueryExecutor;
import dizzybrawl.verticles.PgDatabaseVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@PropertySource("classpath:queries/character-db-queries.properties")
public class PgCharacterNioDao implements CharacterNioDao  {

    private static final Logger log = LoggerFactory.getLogger(PgCharacterNioDao.class);

    private final Environment environment;

    @Autowired
    public PgCharacterNioDao(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void getAllByAccountUUID(Vertx vertx, UUID accountUUID, Handler<AsyncResult<List<Character>>> resultHandler) {
        TupleAsyncQueryExecutor queryExecutor = new TupleAsyncQueryExecutor(environment.getProperty("select-all-characters-by-account-uuid"), Tuple.of(accountUUID));
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                RowSet<Row> queryResult = ar1.result();

                List<Character> characters = new ArrayList<>();
                if (queryResult.rowCount() > 0) {
                    for(Row row : queryResult) {
                        characters.add(new Character(row));
                    }
                }

                resultHandler.handle(Future.succeededFuture(characters));
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, queryExecutor);
    }

    @Override
    public void getAllArmorsByAccountUUID(Vertx vertx, UUID accountUUID, Handler<AsyncResult<List<ConcreteArmor>>> resultHandler) {
        TupleAsyncQueryExecutor queryExecutor = new TupleAsyncQueryExecutor(environment.getProperty("select-all-armors-by-account-uuid"), Tuple.of(accountUUID));
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                List<ConcreteArmor> armors = new ArrayList<>();

                RowSet<Row> queryResult = ar1.result();
                while (queryResult != null) {
                    for (Row row : queryResult) {
                        ConcreteArmor concreteArmor = new ConcreteArmor(row);
                        concreteArmor.setAccountUUID(accountUUID);
                        armors.add(concreteArmor);
                    }

                    queryResult = queryResult.next();
                }

                resultHandler.handle(Future.succeededFuture(armors));
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, queryExecutor);
    }
}
