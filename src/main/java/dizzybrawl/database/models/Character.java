package dizzybrawl.database.models;

import dizzybrawl.database.models.format.JsonTransformable;
import dizzybrawl.database.utils.SqlRowUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 *  Table contains concrete characters information
 */

@Entity
@Table(name = "character")
public class Character implements JsonTransformable {

    public static final String CHARACTER_ID = "character_id";
    public static final String CHARACTER_IS_ENABLED = "is_enabled";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = CHARACTER_ID,
            unique = true,
            nullable = false)
    private Long characterID;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = CharacterType.CHARACTER_TYPE_ID,
                nullable = false)
    private CharacterType characterType;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = Account.ACCOUNT_ID,
            nullable = false)
    private Account account;

    @Column(name = CHARACTER_IS_ENABLED,
            nullable = false)
    private boolean isEnabled;

    public Character() {
        this.characterType = CharacterType.createEmpty();
        this.account = Account.createEmpty();
    }

    public Character(Row sqlRowCharacter) {
        this();

        this.characterID = SqlRowUtils.getElse(sqlRowCharacter, 0L).apply(CHARACTER_ID);
        this.characterType.setId(SqlRowUtils.getElse(sqlRowCharacter, 0).apply(CharacterType.CHARACTER_TYPE_ID));
        this.characterType.setName(SqlRowUtils.getElse(sqlRowCharacter, null, String.class).apply(CharacterType.CHARACTER_NAME));
        this.account.setAccountID(SqlRowUtils.getElse(sqlRowCharacter, 0L).apply(Account.ACCOUNT_ID));
        this.isEnabled = SqlRowUtils.getElse(sqlRowCharacter, false).apply(CHARACTER_IS_ENABLED);
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put(CHARACTER_ID, characterID == null ? null : characterID)
                .put(CharacterType.CHARACTER_TYPE_ID, characterType == null ? 0 : characterType.getId())
                .put(CharacterType.CHARACTER_NAME, characterType == null ? null : characterType.getName())
                .put(Account.ACCOUNT_ID, account.getAccountID() == null ? null : account.getAccountID().toString())
                .put(CHARACTER_IS_ENABLED, isEnabled);
    }

    public Long getCharacterID() {
        return characterID;
    }

    public void setCharacterID(Long characterID) {
        this.characterID = characterID;
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public void setCharacterType(CharacterType characterType) {
        this.characterType = characterType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return isEnabled == character.isEnabled &&
                Objects.equals(characterID, character.characterID) &&
                Objects.equals(characterType, character.characterType) &&
                Objects.equals(account, character.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterID, characterType, account, isEnabled);
    }
}
