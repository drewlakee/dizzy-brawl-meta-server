package dizzybrawl.http.api;

import dizzybrawl.database.models.Task;
import dizzybrawl.http.validation.errors.DataErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import dizzybrawl.verticles.TaskServiceVerticle;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TaskApi {

    public Handler<RoutingContext> onGetAll(Vertx vertx) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            UUID accountUUID;

            try {
                accountUUID = UUID.fromString(requestBodyAsJson.getString("account_uuid"));
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            vertx.eventBus().<EventBusObjectWrapper<List<Task>>>request(TaskServiceVerticle.GET_ALL_ADDRESS, EventBusObjectWrapper.of(accountUUID), ar1 -> {
                if (ar1.succeeded()) {
                    List<Task> tasks = ar1.result().body().get();
                    List<Task> tasksToDelete = new ArrayList<>();
                    JsonArray jsonTasksToResponse = new JsonArray();

                    for (Task task : tasks) {
                        Timestamp generatedDateTimestamp = task.getGeneratedDate();

                        long nowUTCFromEpochSeconds = LocalDateTime.now(Clock.systemUTC()).toEpochSecond(ZoneOffset.UTC);
                        long generatedUTCMomentFromEpochSeconds = generatedDateTimestamp.toLocalDateTime().toEpochSecond(ZoneOffset.UTC);

                        long deltaInMinutes = TimeUnit.SECONDS.toMinutes(nowUTCFromEpochSeconds - generatedUTCMomentFromEpochSeconds);

                        if (deltaInMinutes > task.getActiveInterval()) {
                            tasksToDelete.add(task);
                        } else {
                            JsonObject jsonTask = task.toJson();
                            jsonTask.remove("generated_date");
                            jsonTask.remove("account_uuid");
                            jsonTasksToResponse.add(jsonTask);
                        }
                    }

                    vertx.eventBus().request(TaskServiceVerticle.DELETE_ADDRESS, EventBusObjectWrapper.of(tasksToDelete), ar2 -> {});

                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.put("tasks", jsonTasksToResponse);

                    context.response().end(jsonResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public Handler<RoutingContext> onAdd(Vertx vertx) {
        return context -> {

            if (!context.getBodyAsJson().containsKey("tasks")) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            List<Task> tasksToAdd = new ArrayList<>();
            try {
                for (Object taskObject : context.getBodyAsJson().getJsonArray("tasks")) {
                    JsonObject jsonTask = (JsonObject) taskObject;
                    tasksToAdd.add(new Task(jsonTask));
                }
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            vertx.eventBus().<EventBusObjectWrapper<List<Task>>>request(TaskServiceVerticle.ADD_ADDRESS, EventBusObjectWrapper.of(tasksToAdd), ar1 -> {
                if (ar1.succeeded()) {
                    List<Task> tasksResult = ar1.result().body().get();
                    JsonArray jsonTasksResponse = new JsonArray();

                    for (Task task : tasksResult) {
                        JsonObject jsonResponse = new JsonObject();
                        jsonResponse.put("task_uuid", task.getTaskUUID().toString());
                        jsonTasksResponse.add(jsonResponse);
                    }

                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.put("tasks", jsonTasksResponse);

                    context.response().end(jsonResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public Handler<RoutingContext> onUpdateProgress(Vertx vertx) {
        return context -> {

            if (!context.getBodyAsJson().containsKey("tasks")) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            List<Task> tasksToUpdate = context.getBodyAsJson().getJsonArray("tasks").stream()
                    .map(o -> new Task((JsonObject) o))
                    .collect(Collectors.toList());

            vertx.eventBus().request(TaskServiceVerticle.UPDATE_PROGRESS_ADDRESS, EventBusObjectWrapper.of(tasksToUpdate), ar1 -> {
                if (ar1.succeeded()) {
                    // All was updated
                    context.response().setStatusCode(HttpResponseStatus.OK.code()).end();
                } else {
                    // Error cause DB doesn't store tasks from request
                    context.response().setStatusCode(HttpResponseStatus.PRECONDITION_FAILED.code()).end();
                }
            });
        };
    }
}
