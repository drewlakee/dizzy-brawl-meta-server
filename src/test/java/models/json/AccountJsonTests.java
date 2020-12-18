package models.json;

import com.google.common.collect.ComparisonChain;
import dizzybrawl.database.models.Account;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

public class AccountJsonTests {

    @Test
    public void convertAccountToJson() {
        Account account = new Account();
        account.setAccountID(1L);
        account.setEmail("test00@gmail.com");
        account.setPassword("123456789");
        account.setUsername("test00");

        JsonObject jsonAccount = account.toJson();

        assert jsonAccount != null;
        assert !jsonAccount.isEmpty();
        assert ComparisonChain.start()
                .compare(jsonAccount.getLong(Account.ACCOUNT_ID), account.getAccountID())
                .compare(jsonAccount.getString(Account.EMAIL), account.getEmail())
                .compare(jsonAccount.getString(Account.PASSWORD), account.getPassword())
                .compare(jsonAccount.getString(Account.USERNAME), account.getUsername())
                .result() == 0;
    }

    @Test
    public void convertJsonToAccount() {
        JsonObject jsonAccount = new JsonObject();
        jsonAccount
                .put(Account.ACCOUNT_ID, 1)
                .put(Account.USERNAME, "test")
                .put(Account.PASSWORD, "12345")
                .put(Account.EMAIL, "aaa@gmail.com");

        Account account = new Account(jsonAccount);

        assert !account.isEmpty();
        assert ComparisonChain.start()
                .compare(jsonAccount.getLong(Account.ACCOUNT_ID), account.getAccountID())
                .compare(jsonAccount.getString(Account.EMAIL), account.getEmail())
                .compare(jsonAccount.getString(Account.PASSWORD), account.getPassword())
                .compare(jsonAccount.getString(Account.USERNAME), account.getUsername())
                .result() == 0;
    }
}
