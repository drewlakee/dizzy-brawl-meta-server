package dizzybrawl.http.api;

import dizzybrawl.database.models.Account;
import dizzybrawl.http.validation.errors.DatabaseErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import dizzybrawl.verticles.AccountServiceVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class AccountApi {

    public Handler<RoutingContext> onLogin(Vertx vertx) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            String usernameOrEmail = requestBodyAsJson.getString("username_or_email");
            String password = requestBodyAsJson.getString("password");

            if (usernameOrEmail.trim().isEmpty() || password.trim().isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            vertx.eventBus().<Account>request(AccountServiceVerticle.AUTH_LOGIN_ADDRESS, usernameOrEmail, ar1 -> {
                if (ar1.succeeded()) {
                    Account verifiedAccount = ar1.result().body();
                    JsonObject response;

                    if (verifiedAccount == null || verifiedAccount.isEmpty()) {
                        response = new JsonObject();
                        response.put("error", DatabaseErrors.DOESNT_EXIST_AT_DATABASE);
                    } else {
                        response = verifiedAccount.toJson();

                        if (verifiedAccount.getPassword().equals(password)) {
                            response.remove("password");
                        } else {
                            response.clear();
                            response.put("error", "INVALID_PASSWORD");
                        }
                    }

                    context.response().end(response.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public Handler<RoutingContext> onRegistration(Vertx vertx) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            Account preRegistrationAccount = new Account(requestBodyAsJson);

            if (preRegistrationAccount.isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            vertx.eventBus().<Account>request(AccountServiceVerticle.REGISTRATION_ADDRESS, preRegistrationAccount, ar1 -> {
                if (ar1.succeeded()) {
                    Account verifiedAccount = ar1.result().body();
                    JsonObject response = new JsonObject();

                    if (verifiedAccount.isEmpty()) {
                        response.put("error", DatabaseErrors.ALREADY_EXIST_AT_DATABASE);
                    } else {
                        response.put("account_uuid", verifiedAccount.getAccountUUID().toString());
                    }

                    context.response().end(response.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
