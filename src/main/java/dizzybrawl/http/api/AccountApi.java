package dizzybrawl.http.api;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.models.PreRegistrationAccount;
import dizzybrawl.database.services.AccountService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class AccountApi {

    public static Handler<RoutingContext> onLogin(AccountService accountService) {
        return context -> {
            String usernameOrEmail = context.request().getParam("username_or_email");
            String password = context.request().getParam("password");

            accountService.getAccountByUsernameOrEmail(usernameOrEmail, ar1 -> {
               if (ar1.succeeded()) {
                   Account account = ar1.result();
                   JsonObject response;

                   if (account.isEmpty()) {
                       response = new JsonObject();
                       response.put("found", false);
                   } else {
                       response = account.toJson();
                       if (account.getPassword().equals(password)) {
                           response.put("found", true);
                           response.put("valid", true);
                           response.remove("password");
                       } else {
                           response.clear();
                           response.put("found", true);
                           response.put("valid", false);
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
            PreRegistrationAccount preRegistrationAccount = new PreRegistrationAccount();
            preRegistrationAccount.username = requestBodyAsJson.getString("username");
            preRegistrationAccount.email = requestBodyAsJson.getString("email");
            preRegistrationAccount.password = requestBodyAsJson.getString("password");

            if (preRegistrationAccount.isEmpty()) {
                context.response().end(new JsonObject().put("error", "Not valid data").encodePrettily());
                return;
            }

            accountService.registerAccount(preRegistrationAccount, ar1 -> {
                if (ar1.succeeded()) {
                    Account account = ar1.result();
                    JsonObject response = new JsonObject();

                    if (account.isEmpty()) {
                        response.put("success", false);
                    } else {
                        response.put("account_uuid", account.getAccountUUID().toString());
                        response.put("success", true);
                    }

                    context.response().end(response.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
