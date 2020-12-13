package dizzybrawl.database.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "weapon")
public class Weapon {

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
