package models.sql;

import dizzybrawl.database.models.Account;
import io.vertx.pgclient.impl.RowImpl;
import io.vertx.sqlclient.impl.RowDesc;
import org.junit.Test;

import java.util.List;

public class AccountSqlTests {

    @Test
    public void convertEmptySqlRowToAccount() {
        List<String> accountColumns =
                List.of(Account.ACCOUNT_ID, Account.EMAIL, Account.PASSWORD, Account.USERNAME);
        RowDesc rowDesc = new RowDesc(accountColumns);
        RowImpl row = new RowImpl(rowDesc);

        Account account = new Account(row);

        assert account.isEmpty();
    }
}
