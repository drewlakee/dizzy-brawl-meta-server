package dizzybrawl.http.api;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.models.VerifiedAccount;
import dizzybrawl.database.services.AccountService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

enum AccountErrors {
    INVALID_PASSWORD,
    DOESNT_EXIST_AT_DATABASE,
    ALREADY_EXIST_AT_DATABASE,
    INVALID_ACCOUNT_PARAMETERS
}

public class AccountApi {

    public static Handler<RoutingContext> onLogin(AccountService accountService) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            String usernameOrEmail = requestBodyAsJson.getString("username_or_email");
            String password = requestBodyAsJson.getString("password");

            if (usernameOrEmail.trim().isEmpty() || password.trim().isEmpty()) {
                context.response().end(new JsonObject().put("error", AccountErrors.INVALID_ACCOUNT_PARAMETERS).encodePrettily());
                return;
            }

            accountService.getAccountByUsernameOrEmail(usernameOrEmail, ar1 -> {
               if (ar1.succeeded()) {
                   VerifiedAccount verifiedAccount = ar1.result();
                   JsonObject response;

                   if (verifiedAccount.isEmpty()) {
                       response = new JsonObject();
                       response.put("error", AccountErrors.DOESNT_EXIST_AT_DATABASE);
                   } else {
                       response = verifiedAccount.toJson();

                       if (verifiedAccount.getPassword().equals(password)) {
                           response.remove("password");
                       } else {
                           response.clear();
                           response.put("error", AccountErrors.INVALID_PASSWORD);
                       }
                   }

                   context.response().end(response.encodePrettily());
               } else {
                   context.fail(ar1.cause());
               }
            });
        };
    }

    public static Handler<RoutingContext> onRegistration(AccountService accountService) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            Account preRegistrationVerifiedAccount = new Account(requestBodyAsJson);

            if (preRegistrationVerifiedAccount.isEmpty()) {
                context.response().end(new JsonObject().put("error", AccountErrors.INVALID_ACCOUNT_PARAMETERS).encodePrettily());
                return;
            }

            accountService.registerAccount(preRegistrationVerifiedAccount, ar1 -> {
                if (ar1.succeeded()) {
                    VerifiedAccount verifiedAccount = ar1.result();
                    JsonObject response = new JsonObject();

                    if (verifiedAccount.isEmpty()) {
                        response.put("error", AccountErrors.ALREADY_EXIST_AT_DATABASE);
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
