package dizzybrawl.database.daos;

import dizzybrawl.database.models.Server;
import dizzybrawl.database.wrappers.query.executors.AsyncQueryExecutor;
import dizzybrawl.database.wrappers.query.executors.BatchAtomicAsyncQueryExecutor;
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
@PropertySource(value = "classpath:queries/server-db-queries.properties")
public class PgServerAsyncDao implements ServerAsyncDao {

    private static final Logger log = LoggerFactory.getLogger(PgServerAsyncDao.class);

    private final Environment environment;

    @Autowired
    public PgServerAsyncDao(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void getAll(Vertx vertx, Handler<AsyncResult<List<Server>>> resultHandler) {
        AsyncQueryExecutor queryExecutor = new AsyncQueryExecutor(environment.getProperty("select-all"));
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                RowSet<Row> queryResult = ar1.result();

                List<Server> servers = new ArrayList<>();
                if (queryResult.rowCount() > 0) {
                    for (Row row : queryResult) {
                        servers.add(new Server(row));
                    }
                }
                resultHandler.handle(Future.succeededFuture(servers));
            } else {
                log.error("Can't query database cause ", ar1.cause());
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, queryExecutor);
    }

    @Override
    public void add(Vertx vertx, List<Server> servers, Handler<AsyncResult<List<Server>>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (Server server : servers) {
            server.setServerUUID(UUID.nameUUIDFromBytes(server.getIpV4().getBytes()));
            batch.add(Tuple.of(
                    server.getServerUUID(),
                    server.getIpV4(),
                    server.getGameMode().getGameModeId()
            ));
        }

        BatchAtomicAsyncQueryExecutor queryExecutor = new BatchAtomicAsyncQueryExecutor(environment.getProperty("insert-server"), batch);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                queryExecutor.getTransaction().commit();
                resultHandler.handle(Future.succeededFuture(servers));
            } else {
                queryExecutor.getTransaction().rollback();
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, queryExecutor);
    }

    @Override
    public void delete(Vertx vertx, List<UUID> serverUUIDs, Handler<AsyncResult<Void>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (UUID serverUUID : serverUUIDs) {
            batch.add(Tuple.of(serverUUID));
        }

        BatchAtomicAsyncQueryExecutor queryExecutor =
                new BatchAtomicAsyncQueryExecutor(environment.getProperty("delete-server-by-server-uuid"), batch);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                queryExecutor.getTransaction().commit();
                resultHandler.handle(Future.succeededFuture());
            } else {
                queryExecutor.getTransaction().rollback();
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, queryExecutor);
    }
}
