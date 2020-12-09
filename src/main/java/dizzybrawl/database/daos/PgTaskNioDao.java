package dizzybrawl.database.daos;

import dizzybrawl.database.models.Task;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@PropertySource("classpath:queries/task-db-queries.properties")
public class PgTaskNioDao implements TaskNioDao {

    private static final Logger log = LoggerFactory.getLogger(PgTaskNioDao.class);

    private final Environment environment;

    private final PgPool pgClient;

    @Autowired
    public PgTaskNioDao(PgPool pgPool, Environment environment) {
        this.pgClient = pgPool;
        this.environment = environment;
    }

    @Override
    public void getAllByAccountUUID(UUID accountUUID, Handler<AsyncResult<List<Task>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(environment.getProperty("select-all-tasks-by-account-uuid"))
                        .execute(Tuple.of(accountUUID), ar2 -> {
                            if (ar2.succeeded()) {
                                RowSet<Row> queryResult = ar2.result();

                                List<Task> tasks = new ArrayList<>();
                                if (queryResult.rowCount() > 0) {
                                    for(Row row : queryResult) {
                                        tasks.add(new Task(row));
                                    }
                                }

                                resultHandler.handle(Future.succeededFuture(tasks));
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
    public void delete(List<Task> tasks, Handler<AsyncResult<Void>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                List<Tuple> batch = new ArrayList<>();
                for (Task task : tasks) {
                    batch.add(Tuple.of(task.getTaskUUID()));
                }

                connection
                        .preparedQuery(environment.getProperty("delete-task-by-task-uuid"))
                        .executeBatch(batch, ar2 -> {
                            if (ar2.succeeded()) {
                                resultHandler.handle(Future.succeededFuture());
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
    public void add(List<Task> tasks, Handler<AsyncResult<List<Task>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

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
                            task.getAccount().getAccountUUID())
                    );
                }

                Transaction transaction = connection.begin();
                transaction
                        .preparedQuery(environment.getProperty("insert-task-by-account-uuid"))
                        .executeBatch(batch, ar2 -> {
                            if (ar2.succeeded()) {
                                int countOfInsertedRows = 0;

                                RowSet<Row> currentSetOfTupleInBatch = ar2.result();
                                while (currentSetOfTupleInBatch != null) {
                                    countOfInsertedRows++;
                                    currentSetOfTupleInBatch = currentSetOfTupleInBatch.next();
                                }

                                if (countOfInsertedRows != batch.size()) {
                                    resultHandler.handle(Future.failedFuture(ar2.cause()));
                                    transaction.rollback();
                                } else {
                                    resultHandler.handle(Future.succeededFuture(tasks));
                                    transaction.commit();
                                }
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
    public void updateProgressOf(List<Task> tasks, Handler<AsyncResult<Void>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                List<Tuple> batch = new ArrayList<>();
                for (Task task : tasks) {
                    batch.add(Tuple.of(
                            task.getCurrentState(),
                            task.getTaskUUID()
                    ));
                }

                Transaction transaction = connection.begin();
                transaction
                        .preparedQuery(environment.getProperty("update-task-progress-by-task-uuid"))
                        .executeBatch(batch, ar2 -> {
                            if (ar2.succeeded()) {

                                int updatedRows = 0;
                                RowSet<Row> currentSetOfTupleInBatch = ar2.result();
                                while (currentSetOfTupleInBatch != null) {
                                    updatedRows += currentSetOfTupleInBatch.rowCount();
                                    currentSetOfTupleInBatch = currentSetOfTupleInBatch.next();
                                }

                                if (updatedRows != batch.size()) {
                                    transaction.rollback();
                                    resultHandler.handle(Future.failedFuture(ar2.cause()));
                                } else {
                                    transaction.commit();
                                    resultHandler.handle(Future.succeededFuture());
                                }
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
