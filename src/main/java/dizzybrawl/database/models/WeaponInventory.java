package dizzybrawl.database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class CharactersToWeapons implements Serializable {

    @Column(name = Character.CHARACTER_ID,
            nullable = false)
    private Long characterID;

    @Column(name = Weapon.WEAPON_ID,
            nullable = false)
    private Long weaponId;

    protected CharactersToWeapons() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharactersToWeapons that = (CharactersToWeapons) o;
        return Objects.equals(characterID, that.characterID) && Objects.equals(weaponId, that.weaponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterID, weaponId);
    }
}

@Entity
@Table(name = "weapon_inventory")
public class WeaponInventory {

    @EmbeddedId
    private CharactersToWeapons id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("characterID")
    @JoinColumn(name = Character.CHARACTER_ID)
    private Character character;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("weaponId")
    @JoinColumn(name = Weapon.WEAPON_ID)
    private Weapon weapon;

    @Column(name = ConcreteWeapon.WEAPON_LEVEL,
            nullable = false)
    private int weaponLevel;

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
