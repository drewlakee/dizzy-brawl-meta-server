package dizzybrawl.database.models;

import dizzybrawl.database.models.format.JsonTransformable;
import dizzybrawl.database.utils.SqlRowUtils;
import dizzybrawl.http.utils.JsonUtils;
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

    public static final String TASK_UUID = "task_uuid";
    public static final String TASK_TYPE_ID = "task_type_id";
    public static final String CURRENT_STATE = "current_state";
    public static final String GOAL_STATE = "goal_state";
    public static final String GENERATED_DATE = "generated_date";
    public static final String ACTIVE_INTERVAL = "active_interval";

    @Id
    @Column(name = TASK_UUID,
            unique = true,
            nullable = false)
    private UUID taskUUID;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = Account.ACCOUNT_ID,
                nullable = false)
    private Account account;

    @Column(name = TASK_TYPE_ID,
            nullable = false)
    private int taskTypeId;

    @Column(name = CURRENT_STATE,
            nullable = false)
    private int currentState;

    @Column(name = GOAL_STATE,
            nullable = false)
    private int goalState;

    @Column(name = GENERATED_DATE,
            nullable = false)
    private Timestamp generatedDate;

    @Column(name = ACTIVE_INTERVAL,
            nullable = false)
    private int activeInterval;

    public Task() {
        this.account = Account.createEmpty();
    }

    public Task(JsonObject jsonTask) {
        this();

        Function<String, Integer> getOrElseZero = JsonUtils.getElse(jsonTask, 0);
        Function<String, String> getOrElseNullString = JsonUtils.getElse(jsonTask, null);

        this.taskUUID = getOrElseNullString.apply(TASK_UUID) == null ? null : UUID.fromString(getOrElseNullString.apply(TASK_UUID));
        this.account.setAccountID(Long.valueOf(JsonUtils.getElse(jsonTask, 0).apply(Account.ACCOUNT_ID)));
        this.taskTypeId = getOrElseZero.apply(TASK_TYPE_ID);
        this.currentState = getOrElseZero.apply(CURRENT_STATE);
        this.goalState = getOrElseZero.apply(GOAL_STATE);
        this.generatedDate = JsonUtils.getElse(jsonTask, null, Timestamp.class).apply(GENERATED_DATE);
        this.activeInterval = getOrElseZero.apply(ACTIVE_INTERVAL);
    }

    public Task(Row sqlRowTask) {
        this();

        Function<String, Integer> getOrElseZero = SqlRowUtils.getElse(sqlRowTask, 0);
        Function<String, UUID> getOrElseNullObject = SqlRowUtils.getElse(sqlRowTask, null);

        this.taskUUID = getOrElseNullObject.apply(TASK_UUID);
        this.account.setAccountID(SqlRowUtils.getElse(sqlRowTask, 0L).apply(Account.ACCOUNT_ID));
        this.taskTypeId = getOrElseZero.apply(TASK_TYPE_ID);
        this.currentState = getOrElseZero.apply(CURRENT_STATE);
        this.goalState = getOrElseZero.apply(GOAL_STATE);
        this.generatedDate = Timestamp.valueOf(SqlRowUtils.getElse(sqlRowTask, null, LocalDateTime.class).apply(GENERATED_DATE));
        this.activeInterval = getOrElseZero.apply(ACTIVE_INTERVAL);
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put(TASK_UUID, taskUUID == null ? null : taskUUID.toString())
                .put(Account.ACCOUNT_ID, (account == null || account.getAccountID() == null) ? null : account.getAccountID().toString())
                .put(TASK_TYPE_ID, taskTypeId)
                .put(CURRENT_STATE, currentState)
                .put(GOAL_STATE, goalState)
                .put(GENERATED_DATE, generatedDate == null ? null : generatedDate.toString())
                .put(ACTIVE_INTERVAL, activeInterval);
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
