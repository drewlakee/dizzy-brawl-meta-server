package dizzybrawl.database.daos;

import dizzybrawl.database.models.Server;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ServerNioDao {

    void getAll(Handler<AsyncResult<List<Server>>> resultHandler);

    void add(List<Server> servers, Handler<AsyncResult<List<Server>>> resultHandler);

    void delete(List<UUID> serverUUIDs, Handler<AsyncResult<Void>> resultHandler);
}
