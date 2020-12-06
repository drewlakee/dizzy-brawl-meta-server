package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
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

    private final PgPool pgClient;

    @Autowired
    public PgCharacterNioDao(PgPool pgPool, Environment environment) {
        this.pgClient = pgPool;
        this.environment = environment;
    }

    @Override
    public void getAllByAccountUUID(String accountUUID, Handler<AsyncResult<List<Character>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(environment.getProperty("select-all-characters-by-account-uuid"))
                        .execute(Tuple.of(UUID.fromString(accountUUID)), ar2 -> {
                            if (ar2.succeeded()) {
                                RowSet<Row> queryResult = ar2.result();

                                List<Character> characters = new ArrayList<>();
                                if (queryResult.rowCount() > 0) {
                                    for(Row row : queryResult) {
                                        characters.add(new Character(row));
                                    }
                                }

                                resultHandler.handle(Future.succeededFuture(characters));
                            } else {
                                log.warn("Can't query to database cause " + ar2.cause());
                                resultHandler.handle(Future.failedFuture(ar2.cause()));
                            }

                            connection.close();
                        });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });
    }

    @Override
    public void getAllArmorsByAccountUUID(UUID accountUUID, Handler<AsyncResult<List<ConcreteArmor>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(environment.getProperty("select-all-armors-by-account-uuid"))
                        .execute(Tuple.of(accountUUID), ar2 -> {
                            if (ar2.succeeded()) {
                                List<ConcreteArmor> armors = new ArrayList<>();

                                RowSet<Row> queryResult = ar2.result();
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
                                log.warn("Can't query to database cause " + ar2.cause());
                                resultHandler.handle(Future.failedFuture(ar2.cause()));
                            }

                            connection.close();
                        });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });
    }
}
