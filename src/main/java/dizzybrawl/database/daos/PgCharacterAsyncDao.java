package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import dizzybrawl.database.models.ConcreteWeapon;
import dizzybrawl.database.wrappers.query.executors.BatchAsyncQueryExecutor;
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
public class PgCharacterAsyncDao implements CharacterAsyncDao {

    private static final Logger log = LoggerFactory.getLogger(PgCharacterAsyncDao.class);

    private final Environment environment;

    @Autowired
    public PgCharacterAsyncDao(Environment environment) {
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

    @Override
    public void getAllWeaponsByCharactersUUIDs(Vertx vertx, List<UUID> charactersUUIDs, Handler<AsyncResult<List<ConcreteWeapon>>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (UUID characterUUID : charactersUUIDs) {
            batch.add(Tuple.of(characterUUID));
        }

        BatchAsyncQueryExecutor queryExecutor = new BatchAsyncQueryExecutor(environment.getProperty("select-all-weapons-by-character-uuid"), batch);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                List<ConcreteWeapon> concreteWeapons = new ArrayList<>();

                RowSet<Row> queryResultRows = ar1.result();
                for (int characterIndex = 0; characterIndex < batch.size(); characterIndex++) {
                    for (Row row : queryResultRows) {
                        ConcreteWeapon concreteWeapon = new ConcreteWeapon(row);
                        concreteWeapon.setCharacterUUID(charactersUUIDs.get(characterIndex));
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

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, queryExecutor);
    }
}
