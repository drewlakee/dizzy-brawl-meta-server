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

    @Id
    @Column(name = "armor_id",
            unique = true,
            nullable = false)
    private int armorId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int cost;

    @OneToOne
    @JoinColumn(name = "armor_type_id",
                nullable = false)
    private ArmorType armorType;

    public Armor() {
        this.armorType = ArmorType.createEmpty();
    }

    public Armor(Row sqlRowArmor) {
        this();

        Function<String, Integer> getOrElseZero = SqlRowUtils.getElse(sqlRowArmor, 0);

        this.armorType.setArmorTypeId(getOrElseZero.apply("armor_type_id"));
        this.armorType.setName(SqlRowUtils.getElse(sqlRowArmor, null, String.class).apply("armor_type_name"));
        this.armorId = getOrElseZero.apply("armor_id");
        this.name = SqlRowUtils.getElse(sqlRowArmor, null, String.class).apply("armor_name");
        this.cost = getOrElseZero.apply("cost");
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("armor_id", armorId)
                .put("armor_name", name)
                .put("armor_type", armorType.getName())
                .put("cost", cost);
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
