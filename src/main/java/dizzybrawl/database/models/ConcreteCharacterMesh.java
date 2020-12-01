package dizzybrawl.database.models;

import dizzybrawl.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Objects;
import java.util.UUID;

public class ConcreteCharacterMesh extends CharacterMesh {

    private UUID characterUUID;

    private boolean isEnabled;

    public ConcreteCharacterMesh(Row sqlRowCharacter) {
        super(sqlRowCharacter);
        this.characterUUID = SqlRowUtils.getElse(sqlRowCharacter, null, UUID.class).apply("character_uuid");
        this.isEnabled = SqlRowUtils.getElse(sqlRowCharacter, false).apply("is_enabled");
    }

    @Override
    public JsonObject toJson() {
        return super.toJson()
                .put("character_uuid", characterUUID == null ? null : characterUUID.toString())
                .put("is_enabled", isEnabled);
    }

    public UUID getCharacterUUID() {
        return characterUUID;
    }

    public void setCharacterUUID(UUID characterUUID) {
        this.characterUUID = characterUUID;
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
        ConcreteCharacterMesh that = (ConcreteCharacterMesh) o;
        return isEnabled == that.isEnabled &&
                Objects.equals(characterUUID, that.characterUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), characterUUID, isEnabled);
    }
}
