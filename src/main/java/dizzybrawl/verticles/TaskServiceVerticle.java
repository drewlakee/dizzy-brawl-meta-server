package dizzybrawl.verticles;

import dizzybrawl.database.daos.TaskAsyncDao;
import dizzybrawl.database.models.Task;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS = "task.service";
    public static final String GET_ALL_ADDRESS = ADDRESS + ".get.all";
    public static final String DELETE_ADDRESS = ADDRESS + ".delete";
    public static final String ADD_ADDRESS = ADDRESS + ".add";
    public static final String UPDATE_PROGRESS_ADDRESS = ADDRESS + ".update.progress";

    private final TaskAsyncDao taskAsyncDao;

    @Autowired
    public TaskServiceVerticle(TaskAsyncDao taskAsyncDao) {
        this.taskAsyncDao = taskAsyncDao;
    }

    @Override
    public void start(Promise<Void> startPromise) {

        vertx.eventBus().<EventBusObjectWrapper<Long>>consumer(GET_ALL_ADDRESS, handler -> {
            taskAsyncDao.getAllByAccountUUID(vertx, handler.body().get(), ar1 -> {
                if (ar1.succeeded()) {
                    handler.reply(EventBusObjectWrapper.of(ar1.result()));
                }
            });
        });

        vertx.eventBus().<EventBusObjectWrapper<List<Task>>>consumer(DELETE_ADDRESS, handler -> {
            taskAsyncDao.delete(vertx, handler.body().get(), ar1 -> {
                if (ar1.succeeded()) {
                    handler.reply(ar1.result());
                }
            });
        });

        vertx.eventBus().<EventBusObjectWrapper<List<Task>>>consumer(ADD_ADDRESS, handler -> {
            taskAsyncDao.add(vertx, handler.body().get(), ar1 -> {
                if (ar1.succeeded()) {
                    handler.reply(EventBusObjectWrapper.of(ar1.result()));
                }
            });
        });

        vertx.eventBus().<EventBusObjectWrapper<List<Task>>>consumer(UPDATE_PROGRESS_ADDRESS, handler -> {
           taskAsyncDao.updateProgress(vertx, handler.body().get(), ar1 -> {
               if (ar1.succeeded()) {
                   handler.reply(ar1.result());
               }
           });
        });

        startPromise.complete();
    }
}
