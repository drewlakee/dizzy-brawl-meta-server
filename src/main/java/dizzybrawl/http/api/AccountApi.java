package dizzybrawl.http.api;

import dizzybrawl.database.daos.AccountNioDao;
import dizzybrawl.database.models.Account;
import dizzybrawl.http.validation.errors.DatabaseErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class AccountApi {

    private enum AccountErrors {
        INVALID_PASSWORD
    }

    public Handler<RoutingContext> onLoginHandler(AccountNioDao accountDao) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            String usernameOrEmail = requestBodyAsJson.getString("username_or_email");
            String password = requestBodyAsJson.getString("password");

            if (usernameOrEmail.trim().isEmpty() || password.trim().isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            accountDao.getByUsernameOrEmail(usernameOrEmail, ar1 -> {
               if (ar1.succeeded()) {
                   Account verifiedAccount = ar1.result();
                   JsonObject response;

                   if (verifiedAccount.isEmpty()) {
                       response = new JsonObject();
                       response.put("error", DatabaseErrors.DOESNT_EXIST_AT_DATABASE);
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

    public Handler<RoutingContext> onRegistrationHandler(AccountNioDao accountDao) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            Account preRegistrationAccount = new Account(requestBodyAsJson);

            if (preRegistrationAccount.isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            accountDao.register(preRegistrationAccount, ar1 -> {
                if (ar1.succeeded()) {
                    Account verifiedAccount = ar1.result();
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
