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

    public static final String WEAPON_ID = "weapon_id";
    public static final String WEAPON_NAME = "weapon_name";
    public static final String WEAPON_COST = "weapon_cost";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = WEAPON_ID,
            unique = true,
            nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = CharacterType.CHARACTER_TYPE_ID,
            nullable = false)
    private CharacterType characterType;

    @Column(name = WEAPON_NAME,
            unique = true,
            nullable = false)
    private String name;

    @Column(name = WEAPON_COST,
            nullable = false)
    private int cost;

    public Weapon() {
        this.id = 0L;
        this.characterType = CharacterType.createEmpty();
    }

    public Weapon(Row sqlRowWeapon) {
        this();

        this.id = SqlRowUtils.getElse(sqlRowWeapon, 0L).apply(WEAPON_ID);
        this.characterType.setId(SqlRowUtils.getElse(sqlRowWeapon, 0).apply(CharacterType.CHARACTER_TYPE_ID));
        this.name = SqlRowUtils.getElse(sqlRowWeapon, null, String.class).apply(WEAPON_NAME);
        this.cost = SqlRowUtils.getElse(sqlRowWeapon, 0).apply(WEAPON_COST);
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put(WEAPON_ID, id)
                .put(CharacterType.CHARACTER_TYPE_ID, characterType.getId())
                .put(WEAPON_NAME, name)
                .put(WEAPON_COST, cost);
    }

    public boolean isEmpty() {
        return
                (id == null || id == 0L) &&
                (characterType == null || characterType.getId() == 0) &&
                name == null &&
                cost == 0;
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
