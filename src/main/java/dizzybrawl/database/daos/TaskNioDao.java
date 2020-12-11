package dizzybrawl.database.daos;

import dizzybrawl.database.models.Task;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskNioDao {


    void getAllByAccountUUID(Vertx vertx, UUID accountUUID, Handler<AsyncResult<List<Task>>> resultHandler);

    void delete(Vertx vertx, List<Task> tasks, Handler<AsyncResult<Void>> resultHandler);

    void add(Vertx vertx, List<Task> tasks, Handler<AsyncResult<List<Task>>> resultHandler);

    void updateProgress(Vertx vertx, List<Task> tasks, Handler<AsyncResult<Void>> resultHandler);

}
