package dizzybrawl.database.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Objects;
import java.util.UUID;

@DataObject
public class Character {

    private final UUID characterUUID;
    private final int characterTypeId;
    private final UUID accountUUID;
    private final boolean isEnabled;

    public Character(UUID characterUUID, int characterTypeId,
                     UUID accountUUID, boolean isEnabled) {
        this.characterUUID = characterUUID;
        this.characterTypeId = characterTypeId;
        this.accountUUID = accountUUID;
        this.isEnabled = isEnabled;
    }

    public Character(JsonObject jsonCharacter) {
        this(
                jsonCharacter.getString("character_uuid") == null ? null : UUID.fromString(jsonCharacter.getString("character_uuid")),
                jsonCharacter.getInteger("character_type_id"),
                jsonCharacter.getString("account_uuid") == null ? null : UUID.fromString(jsonCharacter.getString("account_uuid")),
                jsonCharacter.getBoolean("is_enabled")
        );
    }

    public Character(Row sqlRowCharacter) {
        this(
                sqlRowCharacter.getUUID("character_uuid"),
                sqlRowCharacter.getInteger("character_type_id"),
                sqlRowCharacter.getUUID("account_uuid"),
                sqlRowCharacter.getBoolean("is_enabled")
        );
    }

    public static Character createEmpty() {
        return new Character(null, 0, null, false);
    }

    public boolean isEmpty() {
        return
                this.characterUUID == null &&
                this.characterTypeId == 0 &&
                this.accountUUID == null &&
                !this.isEnabled;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("character_uuid", characterUUID == null ? null : characterUUID.toString())
                .put("character_type_id", characterTypeId)
                .put("account_uuid", accountUUID == null ? null : accountUUID.toString())
                .put("is_enabled", isEnabled);
    }

    public UUID getCharacterUUID() {
        return characterUUID;
    }

    public int getCharacterTypeId() {
        return characterTypeId;
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return characterTypeId == character.characterTypeId &&
                isEnabled == character.isEnabled &&
                Objects.equals(characterUUID, character.characterUUID) &&
                Objects.equals(accountUUID, character.accountUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterUUID, characterTypeId, accountUUID, isEnabled);
    }

    @Override
    public String toString() {
        return "Character{" +
                "characterUUID=" + characterUUID +
                ", characterTypeId=" + characterTypeId +
                ", accountUUID=" + accountUUID +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
