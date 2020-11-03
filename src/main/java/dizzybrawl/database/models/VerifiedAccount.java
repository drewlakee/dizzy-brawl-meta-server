package dizzybrawl.database.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import lombok.Getter;

import java.util.UUID;

@Getter
@DataObject
public class VerifiedAccount extends Account {

    private final UUID accountUUID;

    public VerifiedAccount(String username, String password, String email, UUID accountUUID) {
        super(username, password, email);
        this.accountUUID = accountUUID;
    }

    public VerifiedAccount(JsonObject jsonAccount) {
        super(jsonAccount);

        this.accountUUID = jsonAccount.getString("account_uuid") == null ? null : UUID.fromString(jsonAccount.getString("account_uuid"));
    }

    public VerifiedAccount(Row sqlRowAccount) {
        super(sqlRowAccount);

        this.accountUUID = sqlRowAccount.getUUID("account_uuid") == null ? null : sqlRowAccount.getUUID("account_uuid");
    }

    public static VerifiedAccount createEmpty() {
       return new VerifiedAccount(null, null, null, null);
    }

    public boolean isEmpty() {
        return
                super.isEmpty() &&
                this.accountUUID == null;
    }

    public JsonObject toJson() {
        return super.toJson()
                .put("account_uuid", accountUUID == null ? null : accountUUID.toString());
    }
}
