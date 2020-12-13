package dizzybrawl.database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
class AccountsToArmors implements Serializable {

    @Column(name = "account_uuid",
            nullable = false)
    private UUID accountUUID;

    @Column(name = "armor_id",
            nullable = false)
    private int armorId;

    protected AccountsToArmors() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountsToArmors that = (AccountsToArmors) o;
        return armorId == that.armorId &&
                Objects.equals(accountUUID, that.accountUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountUUID, armorId);
    }
}

@Entity
@Table(name = "armor_inventory")
public class ArmorInventory {

    @EmbeddedId
    private AccountsToArmors id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("accountUUID")
    @JoinColumn(name = "account_uuid")
    private Account account;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("armorId")
    @JoinColumn(name = "armor_id")
    private Armor armor;

    @Column(name = "armor_level")
    private int armorLevel;

    protected ArmorInventory() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmorInventory that = (ArmorInventory) o;
        return armorLevel == that.armorLevel &&
                Objects.equals(id, that.id) &&
                Objects.equals(account, that.account) &&
                Objects.equals(armor, that.armor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, armor, armorLevel);
    }
}
