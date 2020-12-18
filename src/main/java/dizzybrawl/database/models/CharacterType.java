package dizzybrawl.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "character_type")
public class CharacterType {

    public static final String CHARACTER_TYPE_ID = "character_type_id";
    public static final String CHARACTER_NAME = "character_name";
    public static final String CHARACTER_IS_ENABLED_AT_BEGIN = "is_enabled_at_begin";

    @Id
    @Column(name = CHARACTER_TYPE_ID,
            unique = true,
            nullable = false)
    private int id;

    @Column(name = CHARACTER_NAME,
            unique = true,
            nullable = false)
    private String name;

    @Column(name = CHARACTER_IS_ENABLED_AT_BEGIN,
            nullable = false)
    private boolean isEnabledAtBegin;

    public CharacterType() {}

    public static CharacterType createEmpty() {
        return new CharacterType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabledAtBegin() {
        return isEnabledAtBegin;
    }

    public void setEnabledAtBegin(boolean enabledAtBegin) {
        isEnabledAtBegin = enabledAtBegin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterType that = (CharacterType) o;
        return id == that.id &&
                isEnabledAtBegin == that.isEnabledAtBegin &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isEnabledAtBegin);
    }
}
