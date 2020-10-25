package dizzybrawl.http.api;

import dizzybrawl.database.models.Task;
import dizzybrawl.database.services.TaskService;
import dizzybrawl.http.Error;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TaskApi {

    public static Handler<RoutingContext> getTasksByAccountUUID(TaskService taskService) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.isEmpty()) {
                context.response().end(new JsonObject().put("error", Error.EMPTY_BODY).encodePrettily());
                return;
            }

            String accountUUIDParam = requestBodyAsJson.getString("account_uuid");

            try {
                UUID.fromString(accountUUIDParam);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                return;
            }

            taskService.getAllTasksByAccountUUID(accountUUIDParam, ar1 -> {
                if (ar1.succeeded()) {
                    List<Task> tasks = ar1.result();
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
                            jsonTask.put("time_spends", deltaInMinutes);
                            jsonTasksToResponse.add(jsonTask);
                        }
                    }

                    taskService.deleteTasks(tasksToDelete, ar2 -> {});

                    context.response().end(jsonTasksToResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public static Handler<RoutingContext> addTasks(TaskService taskService) {
        return context -> {
            JsonArray requestBodyAsJsonArray = context.getBodyAsJsonArray();

            if (requestBodyAsJsonArray.isEmpty()) {
                context.response().end(new JsonObject().put("error", Error.EMPTY_BODY).encodePrettily());
                return;
            }

            List<Task> tasksToAdd = new ArrayList<>();
            try {
                for (Object taskObject : requestBodyAsJsonArray) {
                    JsonObject jsonTask = (JsonObject) taskObject;
                    UUID.fromString(jsonTask.getString("account_uuid"));

                    tasksToAdd.add(new Task(jsonTask));
                }
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                return;
            }

            taskService.addTasks(tasksToAdd, ar1 -> {
                if (ar1.succeeded()) {
                    List<Task> tasksResult = ar1.result();
                    JsonArray jsonTasksResponse = new JsonArray();

                    for (Task task : tasksResult) {
                        JsonObject jsonResponse = new JsonObject();
                        jsonResponse.put("task_uuid", task.getTaskUUID().toString());
                        jsonTasksResponse.add(jsonResponse);
                    }

                    context.response().end(jsonTasksResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public static Handler<RoutingContext> updateTasksProgress(TaskService taskService) {
        return context -> {
            JsonArray requestBodyAsJsonArray = context.getBodyAsJsonArray();

            if (requestBodyAsJsonArray.isEmpty()) {
                context.response().end(new JsonObject().put("error", Error.EMPTY_BODY).encodePrettily());
                return;
            }

            List<Task> tasksToUpdate = new ArrayList<>();
            try {
                requestBodyAsJsonArray.stream()
                        .map(o -> new Task((JsonObject) o))
                        .forEach(tasksToUpdate::add);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                return;
            }

            taskService.updateTasksProgress(tasksToUpdate, ar1 -> {
                if (ar1.succeeded()) {
                    // All was updated
                    context.response().setStatusCode(HttpResponseStatus.OK.code());
                } else {
                    // Error cause DB doesn't store tasks from request
                    context.response().setStatusCode(HttpResponseStatus.PRECONDITION_FAILED.code());
                }

                context.response().end();
            });
        };
    }
}
