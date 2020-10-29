package dizzybrawl.database.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@DataObject
public class CharacterMesh {

    private final int characterMeshId;
    private final int characterTypeId;
    private final int inGameCost;
    private final boolean isEnabled;

    public CharacterMesh(JsonObject jsonCharacterMesh) {
        this.characterMeshId = jsonCharacterMesh.getInteger("character_mesh_id") == null ? 0 : jsonCharacterMesh.getInteger("character_mesh_id");
        this.characterTypeId = jsonCharacterMesh.getInteger("character_type_id") == null ? 0 : jsonCharacterMesh.getInteger("character_type_id");
        this.inGameCost = jsonCharacterMesh.getInteger("in_game_cost") == null ? 0 : jsonCharacterMesh.getInteger("in_game_cost");
        this.isEnabled = jsonCharacterMesh.getBoolean("is_enabled") == null ? false : jsonCharacterMesh.getBoolean("is_enabled");
    }

    public CharacterMesh(Row sqlRowCharacterMesh) {
        this.characterMeshId = sqlRowCharacterMesh.getInteger("character_mesh_id") == null ? 0 : sqlRowCharacterMesh.getInteger("character_mesh_id");
        this.characterTypeId = sqlRowCharacterMesh.getInteger("character_type_id") == null ? 0 : sqlRowCharacterMesh.getInteger("character_type_id");
        this.inGameCost = sqlRowCharacterMesh.getInteger("in_game_cost") == null ? 0 : sqlRowCharacterMesh.getInteger("in_game_cost");
        this.isEnabled = sqlRowCharacterMesh.getBoolean("is_enabled") == null ? false : sqlRowCharacterMesh.getBoolean("is_enabled");
    }

    public static CharacterMesh createEmpty() {
        return new CharacterMesh(0, 0, 0, false);
    }

    public boolean isEmpty() {
        return
                this.characterMeshId == 0 &&
                this.characterTypeId == 0 &&
                this.inGameCost == 0 &&
                !this.isEnabled;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("character_mesh_id", characterMeshId)
                .put("character_type_id", characterTypeId)
                .put("in_game_cost", inGameCost)
                .put("is_enabled", isEnabled);
    }
}