package dizzybrawl.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "armor_type")
public class ArmorType {

    public static final String ARMOR_TYPE_ID = "armor_type_id";
    public static final String ARMOR_TYPE_NAME = "armor_type_name";

    @Id
    @Column(name = ARMOR_TYPE_ID,
            nullable = false)
    private int armorTypeId;

    @Column(name = ARMOR_TYPE_NAME,
            nullable = false)
    private String name;

    public static ArmorType createEmpty() {
        return new ArmorType();
    }

    public int getArmorTypeId() {
        return armorTypeId;
    }

    public void setArmorTypeId(int armorId) {
        this.armorTypeId = armorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmorType armorType = (ArmorType) o;
        return armorTypeId == armorType.armorTypeId &&
                Objects.equals(name, armorType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(armorTypeId, name);
    }
}
