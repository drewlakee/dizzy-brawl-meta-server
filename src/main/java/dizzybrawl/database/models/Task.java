package dizzybrawl.database.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@DataObject
public class Task {

    private final UUID taskUUID;
    private final UUID accountUUID;
    private final int taskTypeId;
    private final int currentState;
    private final int goalState;
    private final Timestamp generatedDate;

    public Task(UUID taskUUID, UUID accountUUID,
                int taskTypeId, int currentState,
                int goalState, Timestamp generatedDate) {
        this.taskUUID = taskUUID;
        this.accountUUID = accountUUID;
        this.taskTypeId = taskTypeId;
        this.currentState = currentState;
        this.goalState = goalState;
        this.generatedDate = generatedDate;
    }

    public Task(JsonObject jsonTask) {
        this(
                jsonTask.getString("task_uuid") == null ? null : UUID.fromString(jsonTask.getString("task_uuid")),
                jsonTask.getString("account_uuid") == null ? null : UUID.fromString(jsonTask.getString("account_uuid")),
                jsonTask.getInteger("task_type_id"),
                jsonTask.getInteger("current_state"),
                jsonTask.getInteger("goal_state"),
                jsonTask.getString("generated_date") == null ? null : Timestamp.valueOf(jsonTask.getString("generated_date"))
        );
    }

    public Task(Row sqlRowTask) {
        this(
                sqlRowTask.getUUID("task_uuid") == null ? null : sqlRowTask.getUUID("task_uuid"),
                sqlRowTask.getUUID("account_uuid") == null ? null : sqlRowTask.getUUID("account_uuid"),
                sqlRowTask.getInteger("task_type_id") == null ? 0 : sqlRowTask.getInteger("task_type_id"),
                sqlRowTask.getInteger("current_state") == null ? 0 : sqlRowTask.getInteger("current_state"),
                sqlRowTask.getInteger("goal_state") == null ? 0 : sqlRowTask.getInteger("goal_state"),
                sqlRowTask.getLocalDateTime("generated_date") == null ? null : Timestamp.valueOf(sqlRowTask.getLocalDateTime("generated_date"))
        );
    }

    public static Task createEmpty() {
        return new Task(null, null, 0, 0, 0, null);
    }

    public boolean isEmpty() {
        return
                this.taskUUID == null &&
                this.accountUUID == null &&
                this.taskTypeId == 0 &&
                this.currentState == 0 &&
                this.goalState == 0 &&
                this.generatedDate == null;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("task_uuid", taskUUID == null ? null : taskUUID.toString())
                .put("account_uuid", accountUUID == null ? null : accountUUID.toString())
                .put("task_type_id", taskTypeId)
                .put("current_state", currentState)
                .put("goal_state", goalState)
                .put("generated_date", generatedDate == null ? null : generatedDate.toString());
    }

    public UUID getTaskUUID() {
        return taskUUID;
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public int getTaskTypeId() {
        return taskTypeId;
    }

    public int getCurrentState() {
        return currentState;
    }

    public int getGoalState() {
        return goalState;
    }

    public Timestamp getGeneratedDate() {
        return generatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskTypeId == task.taskTypeId &&
                currentState == task.currentState &&
                goalState == task.goalState &&
                Objects.equals(taskUUID, task.taskUUID) &&
                Objects.equals(accountUUID, task.accountUUID) &&
                Objects.equals(generatedDate, task.generatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskUUID, accountUUID, taskTypeId, currentState, goalState, generatedDate);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskUUID=" + taskUUID +
                ", accountUUID=" + accountUUID +
                ", taskTypeId=" + taskTypeId +
                ", currentState=" + currentState +
                ", goalState=" + goalState +
                ", generatedDate=" + generatedDate +
                '}';
    }
}
