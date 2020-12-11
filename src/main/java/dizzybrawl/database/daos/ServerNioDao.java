package dizzybrawl.database.daos;

import dizzybrawl.database.models.Server;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServerNioDao {

    void getAll(Vertx vertx, Handler<AsyncResult<List<Server>>> resultHandler);

    void add(Vertx vertx, List<Server> servers, Handler<AsyncResult<List<Server>>> resultHandler);

    void delete(Vertx vertx, List<UUID> serverUUIDs, Handler<AsyncResult<Void>> resultHandler);
}
