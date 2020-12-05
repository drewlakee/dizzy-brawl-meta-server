package dizzybrawl.database.models;

import dizzybrawl.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import javax.persistence.*;
import java.util.Objects;
import java.util.function.Function;


/**
 *  Table contains character's meshes of concrete character type
 *
 *  Example:
 *
 *  WARRIOR - mesh_warrior_00, mesh_warrior_01
 *  ARCHER - mesh_archer_00, mesh_archer01
 *
 */

@Entity
@Table(name = "character_mesh")
public class CharacterMesh implements JsonTransformable {

    @Id
    @Column(name = "character_mesh_id",
            unique = true,
            nullable = false)
    private int characterMeshId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "character_type_id",
            nullable = false)
    private CharacterType characterType;

    @Column(nullable = false)
    private String name;

    @Column(name = "in_game_cost",
            nullable = false)
    private int inGameCost;

    @Column(name = "is_enabled_at_begin",
            nullable = false)
    private boolean isEnabledAtBegin;

    public CharacterMesh() {
        this.characterType = CharacterType.createEmpty();
    }

    public CharacterMesh(Row sqlRowCharacterMesh) {
        this();

        Function<String, Boolean> getOrElseFalse = SqlRowUtils.getElse(sqlRowCharacterMesh, false);
        Function<String, Integer> getOrElseZero = SqlRowUtils.getElse(sqlRowCharacterMesh, 0);

        this.characterMeshId = getOrElseZero.apply("character_mesh_id");
        this.characterType.setId(getOrElseZero.apply("character_type_id"));
        this.inGameCost = getOrElseZero.apply("in_game_cost");
        this.isEnabledAtBegin = getOrElseFalse.apply("is_enabled_at_begin");
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("character_mesh_id", characterMeshId)
                .put("character_type_id", characterType == null ? 0 : characterType.getId())
                .put("in_game_cost", inGameCost);
    }

    public int getCharacterMeshId() {
        return characterMeshId;
    }

    public void setCharacterMeshId(int characterMeshId) {
        this.characterMeshId = characterMeshId;
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public void setCharacterType(CharacterType characterType) {
        this.characterType = characterType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInGameCost() {
        return inGameCost;
    }

    public void setInGameCost(int inGameCost) {
        this.inGameCost = inGameCost;
    }

    public boolean isEnabledAtBegin() {
        return isEnabledAtBegin;
    }

    public void setEnabledAtBegin(boolean enabledAtBegin) {
        isEnabledAtBegin = enabledAtBegin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterMesh that = (CharacterMesh) o;
        return characterMeshId == that.characterMeshId &&
                inGameCost == that.inGameCost &&
                isEnabledAtBegin == that.isEnabledAtBegin &&
                Objects.equals(characterType, that.characterType) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterMeshId, characterType, name, inGameCost, isEnabledAtBegin);
    }
}
