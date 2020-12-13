package dizzybrawl.database.models;

import dizzybrawl.database.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Objects;
import java.util.UUID;

public class ConcreteWeapon extends Weapon {

    private UUID characterUUID;

    private int level;

    private boolean isEnabled;

    public ConcreteWeapon(Row sqlConcreteWeapon) {
        super(sqlConcreteWeapon);

        this.level = SqlRowUtils.getElse(sqlConcreteWeapon, 0).apply("weapon_level");
        this.characterUUID = SqlRowUtils.getElse(sqlConcreteWeapon, null, UUID.class).apply("character_uuid");
        this.isEnabled = SqlRowUtils.getElse(sqlConcreteWeapon, false).apply("is_enabled");
    }

    @Override
    public JsonObject toJson() {
        return super.toJson()
                .put("weapon_level", level)
                .put("character_uuid", characterUUID.toString())
                .put("is_enabled", isEnabled);
    }

    public UUID getCharacterUUID() {
        return characterUUID;
    }

    public void setCharacterUUID(UUID characterUUID) {
        this.characterUUID = characterUUID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
        ConcreteWeapon that = (ConcreteWeapon) o;
        return level == that.level && isEnabled == that.isEnabled && Objects.equals(characterUUID, that.characterUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), characterUUID, level, isEnabled);
    }
}
