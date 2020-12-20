package dizzybrawl.database.daos;

import dizzybrawl.database.models.Task;
import dizzybrawl.database.wrappers.query.executors.BatchAtomicAsyncQueryExecutor;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@PropertySource("classpath:queries/task-db-queries.properties")
public class PgTaskAsyncDao implements TaskAsyncDao {

    private static final Logger log = LoggerFactory.getLogger(PgTaskAsyncDao.class);

    private final Environment environment;

    @Autowired
    public PgTaskAsyncDao(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void getAllByAccountUUID(Vertx vertx, Long accountID, Handler<AsyncResult<List<Task>>> resultHandler) {
        TupleAsyncQueryExecutor queryExecutor = new TupleAsyncQueryExecutor(environment.getProperty("select-all-tasks-by-account-id"), Tuple.of(accountID));
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                RowSet<Row> queryResult = ar1.result();

                List<Task> tasks = new ArrayList<>();
                if (queryResult.rowCount() > 0) {
                    for(Row row : queryResult) {
                        tasks.add(new Task(row));
                    }
                }

                resultHandler.handle(Future.succeededFuture(tasks));
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }

    @Override
    public void delete(Vertx vertx, List<Task> tasks, Handler<AsyncResult<Void>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (Task task : tasks) {
            batch.add(Tuple.of(task.getTaskUUID()));
        }

        BatchAtomicAsyncQueryExecutor queryExecutor = new BatchAtomicAsyncQueryExecutor(environment.getProperty("delete-task-by-task-uuid"), batch);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }

    @Override
    public void add(Vertx vertx, List<Task> tasks, Handler<AsyncResult<List<Task>>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (Task task : tasks) {
            task.setTaskUUID(UUID.randomUUID());

            batch.add(Tuple.of(
                    task.getTaskUUID(),
                    task.getActiveInterval(),
                    task.getCurrentState(),
                    LocalDateTime.now(Clock.systemUTC()),
                    task.getGoalState(),
                    task.getTaskTypeId(),
                    task.getAccount().getAccountID())
            );
        }

        BatchAtomicAsyncQueryExecutor queryExecutor = new BatchAtomicAsyncQueryExecutor(environment.getProperty("insert-task-by-account-uuid"), batch);
        queryExecutor.setHandler(ar1 -> {
            if (ar1.succeeded()) {
                int countOfInsertedRows = 0;

                RowSet<Row> currentSetOfTupleInBatch = ar1.result();
                while (currentSetOfTupleInBatch != null) {
                    countOfInsertedRows++;
                    currentSetOfTupleInBatch = currentSetOfTupleInBatch.next();
                }

                if (countOfInsertedRows != batch.size()) {
                    queryExecutor.getTransaction().rollback();
                    resultHandler.handle(Future.failedFuture(ar1.cause()));
                } else {
                    queryExecutor.getTransaction().commit();
                    resultHandler.handle(Future.succeededFuture(tasks));
                }
            } else {
                log.warn("Can't query to database cause " + ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }

    @Override
    public void updateProgress(Vertx vertx, List<Task> tasks, Handler<AsyncResult<Void>> resultHandler) {
        List<Tuple> batch = new ArrayList<>();
        for (Task task : tasks) {
            batch.add(Tuple.of(
                    task.getCurrentState(),
                    task.getTaskUUID()
            ));
        }

        BatchAtomicAsyncQueryExecutor queryExecutor = new BatchAtomicAsyncQueryExecutor(environment.getProperty("update-task-progress-by-task-uuid"), batch);
        queryExecutor.setHandler(ar2 -> {
            if (ar2.succeeded()) {

                int updatedRows = 0;
                RowSet<Row> currentSetOfTupleInBatch = ar2.result();
                while (currentSetOfTupleInBatch != null) {
                    updatedRows += currentSetOfTupleInBatch.rowCount();
                    currentSetOfTupleInBatch = currentSetOfTupleInBatch.next();
                }

                if (updatedRows != batch.size()) {
                    queryExecutor.getTransaction().rollback();
                    resultHandler.handle(Future.failedFuture(ar2.cause()));
                } else {
                    queryExecutor.getTransaction().commit();
                    resultHandler.handle(Future.succeededFuture());
                }
            } else {
                log.warn("Can't query to database cause " + ar2.cause());
                resultHandler.handle(Future.failedFuture(ar2.cause()));
            }

            queryExecutor.releaseConnection();
        });

        vertx.eventBus().send(PgDatabaseVerticle.QUERY_ADDRESS, EventBusObjectWrapper.of(queryExecutor));
    }
}
