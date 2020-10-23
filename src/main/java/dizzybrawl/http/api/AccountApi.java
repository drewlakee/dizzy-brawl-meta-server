package dizzybrawl.http.api;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.models.PreRegistrationAccount;
import dizzybrawl.database.services.AccountService;
import dizzybrawl.http.Error;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class AccountApi {

    public static Handler<RoutingContext> onLogin(AccountService accountService) {
        return context -> context.vertx().<RoutingContext>executeBlocking(future -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.isEmpty()) {
                context.response().write(new JsonObject().put("error", Error.EMPTY_BODY).encodePrettily());
                future.complete(context);
                return;
            }

            String usernameOrEmail = requestBodyAsJson.getString("username_or_email");
            String password = requestBodyAsJson.getString("password");

            accountService.getAccountByUsernameOrEmail(usernameOrEmail, ar2 -> {
               if (ar2.succeeded()) {
                   Account account = ar2.result();
                   JsonObject response;

                   if (account.isEmpty()) {
                       response = new JsonObject();
                       response.put("error", Error.DOESNT_EXIST_AT_DATABASE);
                   } else {
                       response = account.toJson();

                       if (account.getPassword().equals(password)) {
                           response.remove("password");
                       } else {
                           response.clear();
                           response.put("error", Error.INVALID_PASSWORD);
                       }
                   }

                   context.response().write(response.encodePrettily());

                   future.complete(context);
               } else {
                   future.fail(ar2.cause());
               }
            });
        }, ar1 -> {
            if (ar1.succeeded()) {
                ar1.result().response().end();
            } else {
                ar1.result().fail(ar1.cause());
            }
        });
    }

    public static Handler<RoutingContext> onRegistration(AccountService accountService) {
        return context -> context.vertx().<RoutingContext>executeBlocking(future -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.isEmpty()) {
                context.response().write(new JsonObject().put("error", Error.EMPTY_BODY).encodePrettily());
                future.complete(context);
                return;
            }

            PreRegistrationAccount preRegistrationAccount = new PreRegistrationAccount();
            preRegistrationAccount.username = requestBodyAsJson.getString("username");
            preRegistrationAccount.email = requestBodyAsJson.getString("email");
            preRegistrationAccount.password = requestBodyAsJson.getString("password");

            if (preRegistrationAccount.isEmpty()) {
                context.response().write(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                future.complete(context);
                return;
            }

            accountService.registerAccount(preRegistrationAccount, ar1 -> {
                if (ar1.succeeded()) {
                    Account account = ar1.result();
                    JsonObject response = new JsonObject();

                    if (account.isEmpty()) {
                        response.put("error", Error.ALREADY_EXIST_AT_DATABASE);
                    } else {
                        response.put("account_uuid", account.getAccountUUID().toString());
                    }

                    context.response().write(response.encodePrettily());

                    future.complete(context);
                } else {
                    future.fail(ar1.cause());
                }
            });
        }, ar1 -> {
            if (ar1.succeeded()) {
                ar1.result().response().end();
            } else {
                ar1.result().fail(ar1.cause());
            }
        });
    }
}
