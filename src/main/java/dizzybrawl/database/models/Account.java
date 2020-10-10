package dizzybrawl.database.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Objects;
import java.util.UUID;

@DataObject
public class Account {

    private final UUID accountUUID;
    private final String username;
    private final String password;
    private final String email;

    public Account(UUID accountUUID, String username,
                   String password, String email) {
        this.accountUUID = accountUUID;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Account(JsonObject jsonAccount) {
        this(
                jsonAccount.getString("account_uuid") == null ? null : UUID.fromString(jsonAccount.getString("account_uuid")),
                jsonAccount.getString("username"),
                jsonAccount.getString("password"),
                jsonAccount.getString("email")
        );
    }

    public Account(Row sqlRowAccount) {
        this(
                sqlRowAccount.getUUID("account_uuid"),
                sqlRowAccount.getString("username"),
                sqlRowAccount.getString("password"),
                sqlRowAccount.getString("email")
        );
    }

    public static Account createEmpty() {
        return new Account(null, null, null, null);
    }

    public boolean isEmpty() {
        return
                this.accountUUID == null &&
                this.username == null &&
                this.password == null &&
                this.email == null;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("account_uuid", accountUUID == null ? null : accountUUID.toString())
                .put("username", username)
                .put("password", password)
                .put("email", email);
    }

    public UUID getAccountUUID() {
        return accountUUID;
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

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountUUID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
