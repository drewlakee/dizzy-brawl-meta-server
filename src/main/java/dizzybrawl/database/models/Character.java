package dizzybrawl.database.models;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Character {

    private final UUID characterUUID;
    private final int characterTypeId;
    private final UUID accountUUID;
    private final boolean isEnabled;

    public Character(JsonObject jsonCharacter) {
        this.characterUUID = jsonCharacter.getString("character_uuid") == null ? null : UUID.fromString(jsonCharacter.getString("character_uuid"));
        this.characterTypeId = jsonCharacter.getInteger("character_type_id") == null ? 0 : jsonCharacter.getInteger("character_type_id");
        this.accountUUID = jsonCharacter.getString("account_uuid") == null ? null : UUID.fromString(jsonCharacter.getString("account_uuid"));
        this.isEnabled = jsonCharacter.getBoolean("is_enabled") == null ? false : jsonCharacter.getBoolean("is_enabled");
    }

    public Character(Row sqlRowCharacter) {
        this.characterUUID = sqlRowCharacter.getUUID("character_uuid") == null ? null : sqlRowCharacter.getUUID("character_uuid");
        this.characterTypeId = sqlRowCharacter.getInteger("character_type_id") == null ? 0 : sqlRowCharacter.getInteger("character_type_id");
        this.accountUUID = sqlRowCharacter.getUUID("account_uuid") == null ? null : sqlRowCharacter.getUUID("account_uuid");
        this.isEnabled = sqlRowCharacter.getBoolean("is_enabled") == null ? false : sqlRowCharacter.getBoolean("is_enabled");
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
}
