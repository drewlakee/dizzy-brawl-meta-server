package dizzybrawl.database.models;

import dizzybrawl.database.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Objects;

public class ConcreteArmor extends Armor {

    public static final String ARMOR_LEVEL = "armor_level";
    public static final String ARMOR_IS_ENABLED = "is_enabled";

    private Long characterID;

    private int level;

    private boolean isEnabled;

    public ConcreteArmor(Row sqlRowArmor) {
        super(sqlRowArmor);

        this.level = SqlRowUtils.getElse(sqlRowArmor, 0).apply(ARMOR_LEVEL);
        this.characterID = SqlRowUtils.getElse(sqlRowArmor, 0L).apply(Character.CHARACTER_ID);
        this.isEnabled = SqlRowUtils.getElse(sqlRowArmor, false).apply(ARMOR_IS_ENABLED);
    }

    @Override
    public JsonObject toJson() {
        return super.toJson()
                .put(Character.CHARACTER_ID, characterID == null ? 0 : characterID)
                .put(ARMOR_LEVEL, level)
                .put(ARMOR_IS_ENABLED, isEnabled);
    }

    public Long getCharacterID() {
        return characterID;
    }

    public void setCharacterID(Long characterID) {
        this.characterID = characterID;
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
                Objects.equals(characterID, that.characterID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), characterID, isEnabled);
    }
}
