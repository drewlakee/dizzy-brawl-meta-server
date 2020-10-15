package dizzybrawl.database.services;

import dizzybrawl.database.models.Task;
import dizzybrawl.database.services.impls.PgTaskService;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.SqlClient;

import java.util.List;

@ProxyGen
@VertxGen
public interface TaskService {

    @GenIgnore
    static TaskService create(SqlClient dbClient, Handler<AsyncResult<TaskService>> readyHandler) {
        return new PgTaskService(dbClient, readyHandler);
    }

    @GenIgnore
    static TaskService createProxy(Vertx vertx, String eventBusAddress) {
        return new TaskServiceVertxEBProxy(vertx, eventBusAddress);
    }

    @Fluent
    TaskService getAllTasksByAccountUUID(String accountUUID, Handler<AsyncResult<List<Task>>> resultHandler);

    @Fluent
    TaskService deleteTaskByTaskUUID(String taskUUID, Handler<AsyncResult<Void>> resultHandler);

}
