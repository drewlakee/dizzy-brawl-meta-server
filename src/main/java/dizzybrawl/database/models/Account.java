package dizzybrawl.database.models;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {

    private final String username;
    private final String password;
    private final String email;

    public Account(JsonObject jsonAccount) {
        this.username = jsonAccount.getString("username") == null ? null : jsonAccount.getString("username");
        this.password = jsonAccount.getString("password") == null ? null : jsonAccount.getString("password");
        this.email = jsonAccount.getString("email") == null ? null : jsonAccount.getString("email");
    }

    public Account(Row sqlRowAccount) {
        this.username = sqlRowAccount.getString("username") == null ? null : sqlRowAccount.getString("username");
        this.password = sqlRowAccount.getString("password") == null ? null : sqlRowAccount.getString("password");
        this.email = sqlRowAccount.getString("email") == null ? null : sqlRowAccount.getString("email");
    }

    public boolean isEmpty() {
        return
                (this.username == null || this.username.isEmpty()) &&
                (this.password == null || this.password.isEmpty()) &&
                (this.email == null || this.email.isEmpty());
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("username", username)
                .put("password", password)
                .put("email", email);
    }
}
