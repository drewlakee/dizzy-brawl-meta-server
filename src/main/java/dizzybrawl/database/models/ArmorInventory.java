package dizzybrawl.database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class AccountsToArmors implements Serializable {

    @Column(name = Character.CHARACTER_ID,
            nullable = false)
    private Long characterID;

    @Column(name = Armor.ARMOR_ID,
            nullable = false)
    private int armorId;

    protected AccountsToArmors() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountsToArmors that = (AccountsToArmors) o;
        return armorId == that.armorId &&
                Objects.equals(characterID, that.characterID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterID, armorId);
    }
}

@Entity
@Table(name = "armor_inventory")
public class ArmorInventory {

    @EmbeddedId
    private AccountsToArmors id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("characterID")
    @JoinColumn(name = Character.CHARACTER_ID)
    private Character character;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("armorId")
    @JoinColumn(name = Armor.ARMOR_ID)
    private Armor armor;

    @Column(name = ConcreteArmor.ARMOR_LEVEL)
    private int armorLevel;

    protected ArmorInventory() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmorInventory that = (ArmorInventory) o;
        return armorLevel == that.armorLevel &&
                Objects.equals(id, that.id) &&
                Objects.equals(character, that.character) &&
                Objects.equals(armor, that.armor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, character, armor, armorLevel);
    }
}
