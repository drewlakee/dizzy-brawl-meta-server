package dizzybrawl.database.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject
public class PreRegistrationAccount {

    public String username;
    public String email;
    public String password;

    public PreRegistrationAccount() {}

    public PreRegistrationAccount(JsonObject jsonPreRegistrationAccount) {
        username = jsonPreRegistrationAccount.getString("username");
        email = jsonPreRegistrationAccount.getString("email");
        password = jsonPreRegistrationAccount.getString("password");
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("username", username)
                .put("email", email)
                .put("password", password);
    }

    public boolean isEmpty() {
        return
                username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreRegistrationAccount that = (PreRegistrationAccount) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password);
    }

    @Override
    public String toString() {
        return "PreRegistrationAccount{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
