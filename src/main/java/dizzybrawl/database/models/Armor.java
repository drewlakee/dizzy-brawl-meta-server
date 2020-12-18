package dizzybrawl.database.models;

import dizzybrawl.database.models.format.JsonTransformable;
import dizzybrawl.database.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import javax.persistence.*;
import java.util.Objects;
import java.util.function.Function;

@Entity
@Table(name = "armor")
public class Armor implements JsonTransformable {

    public static final String ARMOR_ID = "armor_id";
    public static final String ARMOR_NAME = "armor_name";
    public static final String ARMOR_COST = "armor_cost";

    @Id
    @Column(name = ARMOR_ID,
            unique = true,
            nullable = false)
    private int armorId;

    @Column(name = ARMOR_NAME,
            nullable = false)
    private String name;

    @Column(name = ARMOR_COST,
            nullable = false)
    private int cost;

    @OneToOne
    @JoinColumn(name = ArmorType.ARMOR_TYPE_ID,
                nullable = false)
    private ArmorType armorType;

    public Armor() {
        this.armorType = ArmorType.createEmpty();
    }

    public Armor(Row sqlRowArmor) {
        this();

        Function<String, Integer> getOrElseZero = SqlRowUtils.getElse(sqlRowArmor, 0);

        this.armorType.setArmorTypeId(getOrElseZero.apply(ArmorType.ARMOR_TYPE_ID));
        this.armorType.setName(SqlRowUtils.getElse(sqlRowArmor, null, String.class).apply(ArmorType.ARMOR_TYPE_NAME));
        this.armorId = getOrElseZero.apply(ARMOR_ID);
        this.name = SqlRowUtils.getElse(sqlRowArmor, null, String.class).apply(ARMOR_NAME);
        this.cost = getOrElseZero.apply(ARMOR_COST);
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put(ARMOR_ID, armorId)
                .put(ARMOR_NAME, name)
                .put(ArmorType.ARMOR_TYPE_NAME, armorType.getName())
                .put(ARMOR_COST, cost);
    }

    public int getArmorId() {
        return armorId;
    }

    public void setArmorId(int armorId) {
        this.armorId = armorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public ArmorType getArmorType() {
        return armorType;
    }

    public void setArmorType(ArmorType armorType) {
        this.armorType = armorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Armor armor = (Armor) o;
        return armorId == armor.armorId &&
                cost == armor.cost &&
                Objects.equals(name, armor.name) &&
                Objects.equals(armorType, armor.armorType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(armorId, name, cost, armorType);
    }
}
