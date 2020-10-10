package dizzybrawl.http.api;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.services.AccountService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class AccountApi {

    public static Handler<RoutingContext> OnLogin(AccountService accountService) {
        return context -> {
            String usernameOrEmail = context.request().getParam("usernameoremail");
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
}
