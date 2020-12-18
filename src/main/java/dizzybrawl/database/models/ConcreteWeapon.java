package dizzybrawl.database.models;

import dizzybrawl.database.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Objects;
import java.util.UUID;

public class ConcreteWeapon extends Weapon {

    public static final String WEAPON_LEVEL = "weapon_level";
    public static final String WEAPON_IS_ENABLED = "is_enabled";

    private Long characterID;

    private int level;

    private boolean isEnabled;

    public ConcreteWeapon(Row sqlConcreteWeapon) {
        super(sqlConcreteWeapon);

        this.level = SqlRowUtils.getElse(sqlConcreteWeapon, 0).apply(WEAPON_LEVEL);
        this.characterID = SqlRowUtils.getElse(sqlConcreteWeapon, 0L).apply(Character.CHARACTER_ID);
        this.isEnabled = SqlRowUtils.getElse(sqlConcreteWeapon, false).apply(WEAPON_IS_ENABLED);
    }

    @Override
    public JsonObject toJson() {
        return super.toJson()
                .put(WEAPON_LEVEL, level)
                .put(Character.CHARACTER_ID, characterID.toString())
                .put(WEAPON_IS_ENABLED, isEnabled);
    }

    public Long getCharacterID() {
        return characterID;
    }

    public void setCharacterID(Long characterID) {
        this.characterID = characterID;
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
        return level == that.level && isEnabled == that.isEnabled && Objects.equals(characterID, that.characterID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), characterID, level, isEnabled);
    }
}
