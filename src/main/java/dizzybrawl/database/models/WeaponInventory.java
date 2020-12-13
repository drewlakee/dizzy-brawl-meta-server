package dizzybrawl.database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
class CharactersToWeapons implements Serializable {

    @Column(name = "character_uuid",
            nullable = false)
    private UUID characterUUID;

    @Column(name = "weapon_id",
            nullable = false)
    private Long weaponId;

    protected CharactersToWeapons() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharactersToWeapons that = (CharactersToWeapons) o;
        return Objects.equals(characterUUID, that.characterUUID) && Objects.equals(weaponId, that.weaponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterUUID, weaponId);
    }
}

@Entity
@Table(name = "weapon_inventory")
public class WeaponInventory {

    @EmbeddedId
    private CharactersToWeapons id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("characterUUID")
    @JoinColumn(name = "character_uuid")
    private Character character;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("weaponId")
    @JoinColumn(name = "weapon_id")
    private Weapon weapon;

    @Column(name = "weapon_level",
            nullable = false)
    private Long weaponLevel;

    protected WeaponInventory() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeaponInventory that = (WeaponInventory) o;
        return Objects.equals(id, that.id) && Objects.equals(character, that.character) && Objects.equals(weapon, that.weapon) && Objects.equals(weaponLevel, that.weaponLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, character, weapon, weaponLevel);
    }
}
