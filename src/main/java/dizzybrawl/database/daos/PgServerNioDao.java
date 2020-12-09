package dizzybrawl.database.daos;

import dizzybrawl.database.models.Server;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;
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
@PropertySource(value = "classpath:queries/server-db-queries.properties")
public class PgServerNioDao implements ServerNioDao {

    private static final Logger log = LoggerFactory.getLogger(PgServerNioDao.class);

    private final Environment environment;

    private final PgPool pgClient;

    @Autowired
    public PgServerNioDao(PgPool pgPool, Environment environment) {
        this.pgClient = pgPool;
        this.environment = environment;
    }

    @Override
    public void getAll(Handler<AsyncResult<List<Server>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .query(environment.getProperty("select-all"))
                        .execute(ar2 -> {
                            if (ar2.succeeded()) {
                                RowSet<Row> queryResult = ar2.result();

                                List<Server> servers = new ArrayList<>();
                                if (queryResult.rowCount() > 0) {
                                    for (Row row : queryResult) {
                                        servers.add(new Server(row));
                                    }
                                }
                                resultHandler.handle(Future.succeededFuture(servers));
                            } else {
                                log.error("Can't query database cause ", ar2.cause());
                            }

                            connection.close();
                        });
            } else {
                log.error("Can't connect to database cause ", ar1.cause());
            }
        });
    }

    @Override
    public void add(List<Server> servers, Handler<AsyncResult<List<Server>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                List<Tuple> batch = new ArrayList<>();
                for (Server server : servers) {
                    server.setServerUUID(UUID.nameUUIDFromBytes(server.getIpV4().getBytes()));
                    batch.add(Tuple.of(
                       server.getServerUUID(),
                       server.getIpV4(),
                       server.getGameMode().getGameModeId()
                    ));
                }

                Transaction transaction = connection.begin();
                transaction
                        .preparedQuery(environment.getProperty("insert-server"))
                        .executeBatch(batch, ar2 -> {
                            if (ar2.succeeded()) {
                                transaction.commit();
                                resultHandler.handle(Future.succeededFuture(servers));
                            } else {
                                resultHandler.handle(Future.failedFuture(ar2.cause()));
                            }

                            connection.close();
                        });
            } else {
                log.error("Can't connect to database cause ", ar1.cause());
            }
        });
    }

    @Override
    public void delete(List<UUID> serverUUIDs, Handler<AsyncResult<Void>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                List<Tuple> batch = new ArrayList<>();
                for (UUID serverUUID : serverUUIDs) {
                    batch.add(Tuple.of(serverUUID));
                }

                Transaction transaction = connection.begin();
                transaction
                        .preparedQuery(environment.getProperty("delete-server-by-server-uuid"))
                        .executeBatch(batch, ar2 -> {
                            if (ar2.succeeded()) {
                                transaction.commit();
                                resultHandler.handle(Future.succeededFuture());
                            } else {
                                transaction.rollback();
                                resultHandler.handle(Future.failedFuture(ar2.cause()));
                            }

                            connection.close();
                        });
            } else {
                log.error("Can't connect to database cause ", ar1.cause());
            }
        });
    }
}
