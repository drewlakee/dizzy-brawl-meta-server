package dizzybrawl.database.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@DataObject
public class Account {

    private final UUID accountUUID;
    private final String username;
    private final String password;
    private final String email;

    public Account(JsonObject jsonAccount) {
        this.accountUUID = jsonAccount.getString("account_uuid") == null ? null : UUID.fromString(jsonAccount.getString("account_uuid"));
        this.username = jsonAccount.getString("username") == null ? null : jsonAccount.getString("username");
        this.password = jsonAccount.getString("password") == null ? null : jsonAccount.getString("password");
        this.email = jsonAccount.getString("email") == null ? null : jsonAccount.getString("email");
    }

    public Account(Row sqlRowAccount) {
        this.accountUUID =sqlRowAccount.getUUID("account_uuid") == null ? null : sqlRowAccount.getUUID("account_uuid");
        this.username = sqlRowAccount.getString("username") == null ? null : sqlRowAccount.getString("username");
        this.password = sqlRowAccount.getString("password") == null ? null : sqlRowAccount.getString("password");
        this.email = sqlRowAccount.getString("email") == null ? null : sqlRowAccount.getString("email");
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
}
