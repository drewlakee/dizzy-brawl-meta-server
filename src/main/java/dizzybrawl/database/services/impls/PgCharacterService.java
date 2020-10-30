package dizzybrawl.database.services.impls;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.CharacterMesh;
import dizzybrawl.database.services.CharacterService;
import dizzybrawl.database.sql.CharacterSqlQuery;
import dizzybrawl.database.sql.SqlLoadable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PgCharacterService implements CharacterService, SqlLoadable<CharacterSqlQuery> {

    private static final Logger log = LoggerFactory.getLogger(PgCharacterService.class);

    private final HashMap<CharacterSqlQuery, String> sqlQueries;
    private final PgPool pgClient;

    public PgCharacterService(SqlClient dbClient, Handler<AsyncResult<CharacterService>> readyHandler) {
        PgPool castResult = null;
        try {
            castResult = (PgPool) dbClient;
        } catch (ClassCastException e) {
            log.error("SQLClient can't be casted to PostgreSQL client.", e.getCause());
            readyHandler.handle(Future.failedFuture(e.getCause()));
        }

        this.pgClient = castResult;
        this.sqlQueries = loadSqlQueries();

        if (sqlQueries.isEmpty()) {
            readyHandler.handle(Future.failedFuture("Service doesn't have SQL queries."));
        }

        readyHandler.handle(Future.succeededFuture(this));
    }

    @Override
    public HashMap<CharacterSqlQuery, String> loadSqlQueries() {
        HashMap<CharacterSqlQuery, String> loadedSqlQueries = new HashMap<>();
        Properties queriesProps = new Properties();

        try (InputStream queriesInputStream = getClass().getResourceAsStream("/character-db-queries.properties")) {
            queriesProps.load(queriesInputStream);

            loadedSqlQueries.put(CharacterSqlQuery.SELECT_ALL_CHARACTERS_BY_ACCOUNT_UUID, queriesProps.getProperty("select-all-characters-by-account-uuid"));
            loadedSqlQueries.put(CharacterSqlQuery.SELECT_ALL_CHARACTER_MESHES_BY_CHARACTER_UUID, queriesProps.getProperty("select-all-character-meshes-by-character-uuid"));
        } catch (IOException e) {
            log.error("Can't load sql queries.", e.getCause());
        }

        return loadedSqlQueries;
    }

    @Override
    public CharacterService getAllCharactersByAccountUUID(String accountUUID, Handler<AsyncResult<List<Character>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(sqlQueries.get(CharacterSqlQuery.SELECT_ALL_CHARACTERS_BY_ACCOUNT_UUID))
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

        return this;
    }


    @Override
    public CharacterService getAllCharacterMeshesByCharacterUUID(List<String> characterUUIDs, Handler<AsyncResult<List<CharacterMesh>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                List<Tuple> batch = new ArrayList<>();
                for (String characterUUID : characterUUIDs) {
                    batch.add(Tuple.of(UUID.fromString(characterUUID)));
                }

                connection
                        .preparedQuery(sqlQueries.get(CharacterSqlQuery.SELECT_ALL_CHARACTER_MESHES_BY_CHARACTER_UUID))
                        .executeBatch(batch, ar2 -> {
                            if (ar2.succeeded()) {
                                List<CharacterMesh> meshes = new ArrayList<>();

                               RowSet<Row> queryResult = ar2.result();
                               while (queryResult != null) {
                                   for (Row row : queryResult) {
                                       meshes.add(new CharacterMesh(row));
                                   }

                                   queryResult = queryResult.next();
                               }


                               resultHandler.handle(Future.succeededFuture(meshes));
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

        return this;
    }
}
