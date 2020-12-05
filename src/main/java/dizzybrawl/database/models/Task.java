package dizzybrawl.database.models;

import dizzybrawl.utils.JsonUtils;
import dizzybrawl.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Entity
@Table(name = "task")
public class Task implements JsonTransformable {

    @Id
    @Column(name = "task_uuid",
            unique = true,
            nullable = false)
    private UUID taskUUID;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_uuid",
                nullable = false)
    private Account account;

    @Column(name = "task_type_id",
            nullable = false)
    private int taskTypeId;

    @Column(name = "current_state",
            nullable = false)
    private int currentState;

    @Column(name = "goal_state",
            nullable = false)
    private int goalState;

    @Column(name = "generated_date",
            nullable = false)
    private Timestamp generatedDate;

    @Column(name = "active_interval",
            nullable = false)
    private int activeInterval;

    public Task() {
        this.account = Account.createEmpty();
    }

    public Task(JsonObject jsonTask) {
        this();

        Function<String, Integer> getOrElseZero = JsonUtils.getElse(jsonTask, 0);
        Function<String, String> getOrElseNullString = JsonUtils.getElse(jsonTask, null);

        this.taskUUID = getOrElseNullString.apply("task_uuid") == null ? null : UUID.fromString(getOrElseNullString.apply("task_uuid"));
        this.account.setAccountUUID(getOrElseNullString.apply("account_uuid") == null ? null : UUID.fromString(getOrElseNullString.apply("account_uuid")));
        this.taskTypeId = getOrElseZero.apply("task_type_id");
        this.currentState = getOrElseZero.apply("current_state");
        this.goalState = getOrElseZero.apply("goal_state");
        this.generatedDate = JsonUtils.getElse(jsonTask, null, Timestamp.class).apply("generated_date");
        this.activeInterval = getOrElseZero.apply("active_interval");
    }

    public Task(Row sqlRowTask) {
        this();

        Function<String, Integer> getOrElseZero = SqlRowUtils.getElse(sqlRowTask, 0);
        Function<String, UUID> getOrElseNullObject = SqlRowUtils.getElse(sqlRowTask, null);

        this.taskUUID = getOrElseNullObject.apply("task_uuid");
        this.account.setAccountUUID(getOrElseNullObject.apply("account_uuid"));
        this.taskTypeId = getOrElseZero.apply("task_type_id");
        this.currentState = getOrElseZero.apply("current_state");
        this.goalState = getOrElseZero.apply("goal_state");
        this.generatedDate = Timestamp.valueOf(SqlRowUtils.getElse(sqlRowTask, null, LocalDateTime.class).apply("generated_date"));
        this.activeInterval = getOrElseZero.apply("active_interval");
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("task_uuid", taskUUID == null ? null : taskUUID.toString())
                .put("account_uuid", (account == null || account.getAccountUUID() == null) ? null : account.getAccountUUID().toString())
                .put("task_type_id", taskTypeId)
                .put("current_state", currentState)
                .put("goal_state", goalState)
                .put("generated_date", generatedDate == null ? null : generatedDate.toString())
                .put("active_interval", activeInterval);
    }

    public UUID getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(UUID taskUUID) {
        this.taskUUID = taskUUID;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(int taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int getGoalState() {
        return goalState;
    }

    public void setGoalState(int goalState) {
        this.goalState = goalState;
    }

    public Timestamp getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Timestamp generatedDate) {
        this.generatedDate = generatedDate;
    }

    public int getActiveInterval() {
        return activeInterval;
    }

    public void setActiveInterval(int activeInterval) {
        this.activeInterval = activeInterval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskTypeId == task.taskTypeId &&
                currentState == task.currentState &&
                goalState == task.goalState &&
                activeInterval == task.activeInterval &&
                Objects.equals(taskUUID, task.taskUUID) &&
                Objects.equals(account, task.account) &&
                Objects.equals(generatedDate, task.generatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskUUID, account, taskTypeId, currentState, goalState, generatedDate, activeInterval);
    }
}
