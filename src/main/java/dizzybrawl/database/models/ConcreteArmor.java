package dizzybrawl.database.models;

import dizzybrawl.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Objects;
import java.util.UUID;

public class ConcreteArmor extends Armor {

    private UUID accountUUID;

    private int level;

    private boolean isEnabled;

    public ConcreteArmor(Row sqlRowArmor) {
        super(sqlRowArmor);

        this.level = SqlRowUtils.getElse(sqlRowArmor, 0).apply("armor_level");
        this.accountUUID = SqlRowUtils.getElse(sqlRowArmor, null, UUID.class).apply("character_uuid");
        this.isEnabled = SqlRowUtils.getElse(sqlRowArmor, false).apply("is_enabled");
    }

    @Override
    public JsonObject toJson() {
        return super.toJson()
                .put("account_uuid", accountUUID == null ? null : accountUUID.toString())
                .put("armor_level", level)
                .put("is_enabled", isEnabled);
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public void setAccountUUID(UUID accountUUID) {
        this.accountUUID = accountUUID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConcreteArmor that = (ConcreteArmor) o;
        return isEnabled == that.isEnabled &&
                Objects.equals(accountUUID, that.accountUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accountUUID, isEnabled);
    }
}
