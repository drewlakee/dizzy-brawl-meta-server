package dizzybrawl.database.models;

import dizzybrawl.database.models.utils.JsonTransformable;
import dizzybrawl.database.utils.SqlRowUtils;
import dizzybrawl.http.utils.JsonUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 *  Table contains user's account information
 */

@Entity
@Table(name = "account")
public class Account implements JsonTransformable {

    @Id
    @Column(name = "account_uuid",
            unique = true,
            nullable = false)
    private UUID accountUUID;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    public Account() {}

    public Account(JsonObject jsonAccount) {
        Function<String, String> getElseNullString = JsonUtils.getElse(jsonAccount, null);

        this.accountUUID = getElseNullString.apply("account_uuid") == null ? null : UUID.fromString(getElseNullString.apply("account_uuid"));
        this.username = getElseNullString.apply("username");
        this.password = getElseNullString.apply("password");
        this.email = getElseNullString.apply("email");
    }

    public Account(Row sqlRowAccount) {
        Function<String, String> getElseNullString = SqlRowUtils.getElse(sqlRowAccount, null);

        this.accountUUID = SqlRowUtils.getElse(sqlRowAccount, null, UUID.class).apply("account_uuid");
        this.username = getElseNullString.apply("username");
        this.password = getElseNullString.apply("password");
        this.email = getElseNullString.apply("email");
    }

    public static Account createEmpty() {
        return new Account();
    }

    public boolean isEmpty() {
        return
                accountUUID == null &&
                username == null &&
                email == null &&
                password == null;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("account_uuid", accountUUID == null ? null : accountUUID.toString())
                .put("username", username)
                .put("password", password)
                .put("email", email);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public void setAccountUUID(UUID accountUUID) {
        this.accountUUID = accountUUID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountUUID, account.accountUUID) &&
                Objects.equals(username, account.username) &&
                Objects.equals(password, account.password) &&
                Objects.equals(email, account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountUUID, username, password, email);
    }
}
