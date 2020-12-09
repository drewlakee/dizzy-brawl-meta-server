package dizzybrawl.database.models;

import dizzybrawl.database.models.utils.JsonTransformable;
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

    @Id
    @Column(name = "character_uuid",
            unique = true,
            nullable = false)
    private UUID characterUUID;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "character_type_id",
                unique = true,
                nullable = false)
    private CharacterType characterType;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_uuid",
            nullable = false)
    private Account account;

    @Column(name = "is_enabled",
            nullable = false)
    private boolean isEnabled;

    public Character() {
        this.characterType = CharacterType.createEmpty();
        this.account = Account.createEmpty();
    }

    public Character(Row sqlRowCharacter) {
        this();

        Function<String, UUID> getElseNullObject = SqlRowUtils.getElse(sqlRowCharacter, null);

        this.characterUUID = getElseNullObject.apply("character_uuid");
        this.characterType.setId(SqlRowUtils.getElse(sqlRowCharacter, 0).apply("character_type_id"));
        this.account.setAccountUUID(getElseNullObject.apply("account_uuid"));
        this.isEnabled = SqlRowUtils.getElse(sqlRowCharacter, false).apply("is_enabled");
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("character_uuid", characterUUID == null ? null : characterUUID.toString())
                .put("character_type_id", characterType == null ? 0 : characterType.getId())
                .put("account_uuid", account.getAccountUUID() == null ? null : account.getAccountUUID().toString())
                .put("is_enabled", isEnabled);
    }

    public UUID getCharacterUUID() {
        return characterUUID;
    }

    public void setCharacterUUID(UUID characterUUID) {
        this.characterUUID = characterUUID;
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
                Objects.equals(characterUUID, character.characterUUID) &&
                Objects.equals(characterType, character.characterType) &&
                Objects.equals(account, character.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterUUID, characterType, account, isEnabled);
    }
}
