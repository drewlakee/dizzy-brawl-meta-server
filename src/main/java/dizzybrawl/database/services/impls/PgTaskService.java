package dizzybrawl.database.services.impls;

import dizzybrawl.database.models.Task;
import dizzybrawl.database.services.TaskService;
import dizzybrawl.database.sql.SqlLoadable;
import dizzybrawl.database.sql.TaskSqlQuery;
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

public class PgTaskService implements TaskService, SqlLoadable<TaskSqlQuery> {

    private static final Logger log = LoggerFactory.getLogger(PgTaskService.class);

    private final HashMap<TaskSqlQuery, String> sqlQueries;
    private final PgPool pgClient;

    public PgTaskService(SqlClient dbClient, Handler<AsyncResult<TaskService>> readyHandler) {
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
    public HashMap<TaskSqlQuery, String> loadSqlQueries() {
        HashMap<TaskSqlQuery, String> loadedSqlQueries = new HashMap<>();
        Properties queriesProps = new Properties();

        try (InputStream queriesInputStream = getClass().getResourceAsStream("/task-db-queries.properties")) {
            queriesProps.load(queriesInputStream);

            loadedSqlQueries.put(TaskSqlQuery.SELECT_ALL_TASKS_BY_ACCOUNT_UUID, queriesProps.getProperty("select-all-tasks-by-account-uuid"));
            loadedSqlQueries.put(TaskSqlQuery.DELETE_TASK_BY_TASK_UUID, queriesProps.getProperty("delete-task-by-task-uuid"));
        } catch (IOException e) {
            log.error("Can't load sql queries.", e.getCause());
        }

        return loadedSqlQueries;
    }

    @Override
    public TaskService getAllTasksByAccountUUID(String accountUUID, Handler<AsyncResult<List<Task>>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(sqlQueries.get(TaskSqlQuery.SELECT_ALL_TASKS_BY_ACCOUNT_UUID))
                        .execute(Tuple.of(UUID.fromString(accountUUID)), ar2 -> {
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
                        });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });

        return this;
    }

    @Override
    public TaskService deleteTaskByTaskUUID(String taskUUID, Handler<AsyncResult<Void>> resultHandler) {
        pgClient.getConnection(ar1 -> {
            if (ar1.succeeded()) {
                SqlConnection connection = ar1.result();

                connection
                        .preparedQuery(sqlQueries.get(TaskSqlQuery.DELETE_TASK_BY_TASK_UUID))
                        .execute(Tuple.of(UUID.fromString(taskUUID)), ar2 -> {
                            if (ar2.succeeded()) {
                                resultHandler.handle(Future.succeededFuture());
                            } else {
                                log.warn("Can't query to database cause " + ar2.cause());
                                resultHandler.handle(Future.failedFuture(ar2.cause()));
                            }
                        });
            } else {
                log.error("Can't connect to database.", ar1.cause());
                resultHandler.handle(Future.failedFuture(ar1.cause()));
            }
        });

        return this;
    }
}
