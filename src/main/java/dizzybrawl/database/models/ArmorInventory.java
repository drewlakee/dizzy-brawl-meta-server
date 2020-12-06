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

    public UUID getAccountUUID() {
        return accountUUID;
    }

    protected void setAccountUUID(UUID characterUUID) {
        this.accountUUID = characterUUID;
    }

    public int getArmorId() {
        return armorId;
    }

    protected void setArmorId(int characterMeshId) {
        this.armorId = characterMeshId;
    }

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

    public AccountsToArmors getId() {
        return id;
    }

    public void setId(AccountsToArmors id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public int getArmorLevel() {
        return armorLevel;
    }

    public void setArmorLevel(int armorLevel) {
        this.armorLevel = armorLevel;
    }

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
