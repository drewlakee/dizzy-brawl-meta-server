package dizzybrawl.verticles;

import dizzybrawl.database.daos.ServerAsyncDao;
import dizzybrawl.database.models.Server;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ServerServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS = "server.service";
    public static final String ADD_ADDRESS = ADDRESS + ".add";
    public static final String GET_ALL_ADDRESS = ADDRESS + ".get.all";
    public static final String DELETE_ADDRESS = ADDRESS + ".delete";

    private final ServerAsyncDao serverAsyncDao;

    @Autowired
    public ServerServiceVerticle(ServerAsyncDao serverAsyncDao) {
        this.serverAsyncDao = serverAsyncDao;
    }

    @Override
    public void start(Promise<Void> startPromise) {

        vertx.eventBus().<List<Server>>consumer(ADD_ADDRESS, handler -> {
           serverAsyncDao.add(vertx, handler.body(), ar1 -> {
               if (ar1.succeeded()) {
                   handler.reply(ar1.result());
               }
           });
        });

        vertx.eventBus().consumer(GET_ALL_ADDRESS, handler -> {
            serverAsyncDao.getAll(vertx, ar1 -> {
                if (ar1.succeeded()) {
                    handler.reply(ar1.result());
                }
            });
        });

        vertx.eventBus().<List<UUID>>consumer(DELETE_ADDRESS, handler -> {
           serverAsyncDao.delete(vertx, handler.body(), ar1 -> {
               if (ar1.succeeded()) {
                   handler.reply(ar1.result());
               }
           });
        });

        startPromise.complete();
    }
}
