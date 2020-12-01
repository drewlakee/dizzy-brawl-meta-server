package dizzybrawl.database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 *   Embedded keys for meshes inventory table
 */

@Embeddable
class CharactersToMeshes implements Serializable {

    @Column(name = "character_uuid",
            nullable = false)
    private UUID characterUUID;

    @Column(name = "character_mesh_id",
            nullable = false)
    private int characterMeshId;

    protected CharactersToMeshes() {}

    public UUID getCharacterUUID() {
        return characterUUID;
    }

    protected void setCharacterUUID(UUID characterUUID) {
        this.characterUUID = characterUUID;
    }

    public int getCharacterMeshId() {
        return characterMeshId;
    }

    protected void setCharacterMeshId(int characterMeshId) {
        this.characterMeshId = characterMeshId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharactersToMeshes that = (CharactersToMeshes) o;
        return characterMeshId == that.characterMeshId &&
                Objects.equals(characterUUID, that.characterUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterUUID, characterMeshId);
    }
}

/**
 *  Table contains available meshes of concrete character
 *
 *  Example:
 *
 *  character00 - has mesh00, mesh06
 *  character01 - has mesh00, mesh01
 *
 *  if some character hasn't some meshes in that table
 *  this means that user's doesn't have any meshes for his concrete character
 *
 */

@Entity
@Table(name = "character_mesh_inventory")
public class CharacterMeshInventory {

    @EmbeddedId
    CharactersToMeshes id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("characterUUID")
    @JoinColumn(name = "character_uuid")
    Character character;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("characterMeshId")
    @JoinColumn(name = "character_mesh_id")
    CharacterMesh characterMesh;

    @Column(name = "is_enabled",
            nullable = false)
    boolean isEnabled;

    protected CharacterMeshInventory() {}

    public CharactersToMeshes getId() {
        return id;
    }

    protected void setId(CharactersToMeshes id) {
        this.id = id;
    }

    public Character getCharacter() {
        return character;
    }

    protected void setCharacter(Character character) {
        this.character = character;
    }

    public CharacterMesh getCharacterMesh() {
        return characterMesh;
    }

    protected void setCharacterMesh(CharacterMesh characterMesh) {
        this.characterMesh = characterMesh;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    protected void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterMeshInventory that = (CharacterMeshInventory) o;
        return isEnabled == that.isEnabled &&
                Objects.equals(id, that.id) &&
                Objects.equals(character, that.character) &&
                Objects.equals(characterMesh, that.characterMesh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, character, characterMesh, isEnabled);
    }
}
