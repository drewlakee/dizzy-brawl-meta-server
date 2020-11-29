package dizzybrawl.database.models;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Task {

    private final UUID taskUUID;
    private final UUID accountUUID;
    private final int taskTypeId;
    private final int currentState;
    private final int goalState;
    private final Timestamp generatedDate;
    private final int activeInterval;

    public Task(JsonObject jsonTask) {
        this.taskUUID = jsonTask.getString("task_uuid") == null ? null : UUID.fromString(jsonTask.getString("task_uuid"));
        this.accountUUID = jsonTask.getString("account_uuid") == null ? null : UUID.fromString(jsonTask.getString("account_uuid"));
        this.taskTypeId = jsonTask.getInteger("task_type_id") == null ? 0 : jsonTask.getInteger("task_type_id");
        this.currentState = jsonTask.getInteger("current_state") == null ? 0 : jsonTask.getInteger("current_state");
        this.goalState = jsonTask.getInteger("goal_state") == null ? 0 : jsonTask.getInteger("goal_state");
        this.generatedDate = jsonTask.getString("generated_date") == null ? null : Timestamp.valueOf(jsonTask.getString("generated_date"));
        this.activeInterval = jsonTask.getInteger("active_interval")  == null ? 0 : jsonTask.getInteger("active_interval");
    }

    public Task(Row sqlRowTask) {
        this.taskUUID = sqlRowTask.getUUID("task_uuid") == null ? null : sqlRowTask.getUUID("task_uuid");
        this.accountUUID = sqlRowTask.getUUID("account_uuid") == null ? null : sqlRowTask.getUUID("account_uuid");
        this.taskTypeId = sqlRowTask.getInteger("task_type_id") == null ? 0 : sqlRowTask.getInteger("task_type_id");
        this.currentState = sqlRowTask.getInteger("current_state") == null ? 0 : sqlRowTask.getInteger("current_state");
        this.goalState = sqlRowTask.getInteger("goal_state") == null ? 0 : sqlRowTask.getInteger("goal_state");
        this.generatedDate = sqlRowTask.getLocalDateTime("generated_date") == null ? null : Timestamp.valueOf(sqlRowTask.getLocalDateTime("generated_date"));
        this.activeInterval = sqlRowTask.getInteger("active_interval") == null ? 0 : sqlRowTask.getInteger("active_interval");
    }

    public static Task createEmpty() {
        return new Task(null, null, 0, 0, 0, null, 0);
    }

    public boolean isEmpty() {
        return
                this.taskUUID == null &&
                this.accountUUID == null &&
                this.taskTypeId == 0 &&
                this.currentState == 0 &&
                this.goalState == 0 &&
                this.generatedDate == null &&
                this.activeInterval == 0;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("task_uuid", taskUUID == null ? null : taskUUID.toString())
                .put("account_uuid", accountUUID == null ? null : accountUUID.toString())
                .put("task_type_id", taskTypeId)
                .put("current_state", currentState)
                .put("goal_state", goalState)
                .put("generated_date", generatedDate == null ? null : generatedDate.toString())
                .put("active_interval", activeInterval);
    }
}
