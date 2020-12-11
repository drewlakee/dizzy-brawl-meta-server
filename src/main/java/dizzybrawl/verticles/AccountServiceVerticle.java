package dizzybrawl.verticles;

import dizzybrawl.database.daos.AccountAsyncDao;
import dizzybrawl.database.models.Account;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS = "account.service";
    public static final String AUTH_LOGIN_ADDRESS = ADDRESS + ".auth.login";
    public static final String REGISTRATION_ADDRESS = ADDRESS + ".registration";

    private final AccountAsyncDao accountAsyncDao;

    @Autowired
    public AccountServiceVerticle(AccountAsyncDao accountAsyncDao) {
        this.accountAsyncDao = accountAsyncDao;
    }

    @Override
    public void start(Promise<Void> startPromise) {

        vertx.eventBus().<String>consumer(AUTH_LOGIN_ADDRESS, handler -> {
            accountAsyncDao.getByUsernameOrEmail(vertx, handler.body(), ar1 -> {
                if (ar1.succeeded()) {
                    handler.reply(ar1.result());
                }
            });
        });

        vertx.eventBus().<Account>consumer(REGISTRATION_ADDRESS, handler -> {
           accountAsyncDao.register(vertx, handler.body(), ar1 -> {
               if (ar1.succeeded()) {
                   handler.reply(ar1.result());
               }
           });
        });

        startPromise.complete();
    }
}
