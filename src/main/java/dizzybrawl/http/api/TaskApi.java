package dizzybrawl.http.api;

import dizzybrawl.database.models.Task;
import dizzybrawl.database.services.TaskService;
import dizzybrawl.http.Error;
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
import java.util.stream.Collectors;

public class TaskApi {

    /**
     *  Interval computing.
     *
     *  Example:
     *
     *      Generated date of task is 25.08 23:30
     *      In next day user comes and takes his tasks in 26.08 8:30
     *
     *      Time spends - 9 hours = 540 minutes
     *
     *      If (Time spends > IntervalInMinutes) then delete task in DB
     *                                           else return in response
     */

    public static Handler<RoutingContext> getTasksByAccountUUIDWithIntervalInMinutes(TaskService taskService) {
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
                    JsonArray jsonTasksInIntervalResponse = new JsonArray();

                    for (Task task : tasks) {
                        Timestamp generatedDateTimestamp = task.getGeneratedDate();

                        long nowFromEpochSeconds = LocalDateTime.now(Clock.systemUTC()).toEpochSecond(ZoneOffset.UTC);
                        long generatedMomentFromEpochSeconds = generatedDateTimestamp.toLocalDateTime().toEpochSecond(ZoneOffset.UTC);
                        long deltaInMinutes = TimeUnit.SECONDS.toMinutes(nowFromEpochSeconds - generatedMomentFromEpochSeconds);

                        if (deltaInMinutes > task.getActiveInterval()) {
                            tasksToDelete.add(task);
                        } else {
                            JsonObject jsonTask = task.toJson();
                            jsonTask.remove("generated_date");
                            jsonTask.put("time_spends", deltaInMinutes);
                            jsonTasksInIntervalResponse.add(jsonTask);
                        }
                    }

                    taskService.deleteTasks(tasksToDelete, ar2 -> {});

                    context.response().end(jsonTasksInIntervalResponse.encodePrettily());
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

            List<Task> tasks = new ArrayList<>();
            try {
                for (Object taskObject : requestBodyAsJsonArray) {
                    JsonObject jsonTask = (JsonObject) taskObject;
                    UUID.fromString(jsonTask.getString("account_uuid"));

                    tasks.add(new Task(jsonTask));
                }
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                return;
            }

            taskService.addTasks(tasks, ar1 -> {
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

    public static Handler<RoutingContext> updateTasks(TaskService taskService) {
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
                    context.response().setStatusCode(200).end();
                } else {
                    // maybe server hasn't any user's input tasks
                    context.response().setStatusCode(412).end();
                }
            });
        };
    }
}
