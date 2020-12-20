package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import dizzybrawl.database.models.ConcreteWeapon;
import dizzybrawl.database.wrappers.query.executors.BatchAsyncQueryExecutor;
import dizzybrawl.database.wrappers.query.executors.TupleAsyncQueryExecutor;
import dizzybrawl.verticles.PgDatabaseVerticle;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
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

@Repository
@PropertySource("classpath:queries/character-db-queries.properties")
public class PgCharacterAsyncDao implements CharacterAsyncDao {

    private static final Logger log = LoggerFactory.getLogger(PgCharacterAsyncDao.class);

    private final Environment environment;

    @Autowired
    public PgCharacterAsyncDao(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void getAllByAccountID(Vertx vertx, Long accountID, Handler<AsyncResult<List<Character>>> resultHandler) {
        TupleAsyncQueryExecutor queryExecutor = new TupleAsyncQueryExecutor(environment.getProperty("select-all-characters-by-account-id"), Tuple.of(accountID));
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

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }

    @Override
    public void getAllArmorsByAccountID(Vertx vertx, List<Long> charactersIDs, Handler<AsyncResult<List<ConcreteArmor>>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (Long characterID : charactersIDs) {
            batch.add(Tuple.of(characterID));
        }

        BatchAsyncQueryExecutor queryExecutor = new BatchAsyncQueryExecutor(environment.getProperty("select-all-armors-by-character-id"), batch);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                List<ConcreteArmor> concreteArmors = new ArrayList<>();

                RowSet<Row> queryResultRows = ar1.result();
                for (int characterIndex = 0; characterIndex < batch.size(); characterIndex++) {
                    for (Row row : queryResultRows) {
                        ConcreteArmor concreteArmor = new ConcreteArmor(row);
                        concreteArmor.setCharacterID(charactersIDs.get(characterIndex));
                        concreteArmors.add(concreteArmor);
                    }
                    queryResultRows = queryResultRows.next();
                }

                resultHandler.handle(Future.succeededFuture(concreteArmors));
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }

    @Override
    public void getAllWeaponsByCharactersUUIDs(Vertx vertx, List<Long> charactersIDs, Handler<AsyncResult<List<ConcreteWeapon>>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (Long characterID : charactersIDs) {
            batch.add(Tuple.of(characterID));
        }

        BatchAsyncQueryExecutor queryExecutor = new BatchAsyncQueryExecutor(environment.getProperty("select-all-weapons-by-character-id"), batch);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                List<ConcreteWeapon> concreteWeapons = new ArrayList<>();

                RowSet<Row> queryResultRows = ar1.result();
                for (int characterIndex = 0; characterIndex < batch.size(); characterIndex++) {
                    for (Row row : queryResultRows) {
                        ConcreteWeapon concreteWeapon = new ConcreteWeapon(row);
                        concreteWeapon.setCharacterID(charactersIDs.get(characterIndex));
                        concreteWeapons.add(concreteWeapon);
                    }
                    queryResultRows = queryResultRows.next();
                }

                resultHandler.handle(Future.succeededFuture(concreteWeapons));
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }
}
