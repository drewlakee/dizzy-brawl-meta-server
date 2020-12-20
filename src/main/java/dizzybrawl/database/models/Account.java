package dizzybrawl.database.models;

import dizzybrawl.database.models.format.JsonTransformable;
import dizzybrawl.database.utils.SqlRowUtils;
import dizzybrawl.http.utils.JsonUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import javax.persistence.*;
import java.util.Objects;
import java.util.function.Function;

/**
 *  Table contains user's account information
 */

@Entity
@Table(name = "account")
public class Account implements JsonTransformable {

    public static final String ACCOUNT_ID = "account_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ACCOUNT_ID,
            unique = true,
            nullable = false)
    private Long accountID;

    @Column(name = USERNAME,
            unique = true,
            nullable = false)
    private String username;

    @Column(name = PASSWORD,
            nullable = false)
    private String password;

    @Column(name = EMAIL,
            unique = true,
            nullable = false)
    private String email;

    public Account() {}

    public Account(JsonObject jsonAccount) {
        Function<String, String> getElseNullString = JsonUtils.getElse(jsonAccount, null);

        this.accountID = Long.valueOf(JsonUtils.getElse(jsonAccount, 0).apply(ACCOUNT_ID));
        this.username = getElseNullString.apply(USERNAME);
        this.password = getElseNullString.apply(PASSWORD);
        this.email = getElseNullString.apply(EMAIL);
    }

    public Account(Row sqlRowAccount) {
        Function<String, String> getElseNullString = SqlRowUtils.getElse(sqlRowAccount, null);

        this.accountID = SqlRowUtils.getElse(sqlRowAccount, 0L).apply(ACCOUNT_ID);
        this.username = getElseNullString.apply(USERNAME);
        this.password = getElseNullString.apply(PASSWORD);
        this.email = getElseNullString.apply(EMAIL);
    }

    public static Account createEmpty() {
        return new Account();
    }

    public boolean isEmpty() {
        return
                (accountID == null || accountID == 0L) &&
                username == null &&
                email == null &&
                password == null;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put(ACCOUNT_ID, accountID == null ? 0 : accountID)
                .put(USERNAME, username)
                .put(PASSWORD, password)
                .put(EMAIL, email);
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

    public Long getAccountID() {
        return accountID;
    }

    public void setAccountID(Long accountID) {
        this.accountID = accountID;
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
        return Objects.equals(accountID, account.accountID) &&
                Objects.equals(username, account.username) &&
                Objects.equals(password, account.password) &&
                Objects.equals(email, account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID, username, password, email);
    }
}
