package dizzybrawl.database.daos;

import dizzybrawl.database.models.Task;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskNioDao {


    void getAllByAccountUUID(String accountUUID, Handler<AsyncResult<List<Task>>> resultHandler);

    void delete(List<Task> tasks, Handler<AsyncResult<Void>> resultHandler);

    void add(List<Task> tasks, Handler<AsyncResult<List<Task>>> resultHandler);

    void updateProgressOf(List<Task> tasks, Handler<AsyncResult<Void>> resultHandler);

}
