package dizzybrawl.database.models;

import dizzybrawl.database.models.format.JsonTransformable;
import dizzybrawl.database.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "weapon")
public class Weapon implements JsonTransformable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weapon_id",
            unique = true,
            nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "character_type_id",
            nullable = false)
    private CharacterType characterType;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private int cost;

    public Weapon() {
        this.id = 0L;
        this.characterType = CharacterType.createEmpty();
    }

    public Weapon(Row sqlRowWeapon) {
        this();

        this.id = SqlRowUtils.getElse(sqlRowWeapon, 0L).apply("weapon_id");
        this.characterType.setId(SqlRowUtils.getElse(sqlRowWeapon, 0).apply("character_type_id"));
        this.name = SqlRowUtils.getElse(sqlRowWeapon, null, String.class).apply("weapon_name");
        this.cost = SqlRowUtils.getElse(sqlRowWeapon, 0).apply("weapon_cost");
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("weapon_id", id)
                .put("character_type_id", characterType.getId())
                .put("weapon_name", name)
                .put("weapon_cost", cost);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weapon weapon = (Weapon) o;
        return cost == weapon.cost && Objects.equals(id, weapon.id) && Objects.equals(characterType, weapon.characterType) && Objects.equals(name, weapon.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, characterType, name, cost);
    }
}
