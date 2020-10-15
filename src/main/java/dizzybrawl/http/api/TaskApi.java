package dizzybrawl.http.api;

import dizzybrawl.database.models.Task;
import dizzybrawl.database.services.TaskService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
            String accountUUIDParam = context.request().getParam("account_uuid");
            String intervalInMinutesParam = context.request().getParam("interval");

            try {
                UUID.fromString(accountUUIDParam);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", "Wrong account uuid format").encodePrettily());
                return;
            }

            int intervalInMinutes;
            try {
                intervalInMinutes = Integer.parseInt(intervalInMinutesParam);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", "Wrong interval format").encodePrettily());
                return;
            }

            if (intervalInMinutes < 0) {
                context.response().end(new JsonObject().put("error", "Wrong interval - " + intervalInMinutes).encodePrettily());
                return;
            }

            int finalIntervalInMinutes = intervalInMinutes;
            taskService.getAllTasksByAccountUUID(accountUUIDParam, ar1 -> {
                if (ar1.succeeded()) {
                    List<Task> tasks = ar1.result();
                    JsonArray jsonTasksInIntervalResponse = new JsonArray();

                    for (Task task : tasks) {
                        Timestamp generatedDateTimestamp = task.getGeneratedDate();

                        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
                        LocalDateTime generatedTime = generatedDateTimestamp.toLocalDateTime();
                        int deltaInMinutes = now.getMinute() - generatedTime.getMinute();

                        if (deltaInMinutes > finalIntervalInMinutes) {
                            taskService.deleteTaskByTaskUUID(task.getTaskUUID().toString(), ar2 -> {});
                        } else {
                            JsonObject jsonTask = task.toJson();
                            jsonTask.put("time_spends", deltaInMinutes);
                            jsonTasksInIntervalResponse.add(jsonTask);
                        }
                    }

                    context.response().end(jsonTasksInIntervalResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
