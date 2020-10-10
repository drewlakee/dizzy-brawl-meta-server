package dizzybrawl.database.services;

import dizzybrawl.database.models.Account;
import dizzybrawl.database.models.PreRegistrationAccount;
import dizzybrawl.database.services.impls.PgAccountService;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.SqlClient;

@ProxyGen
@VertxGen
public interface AccountService {

    @GenIgnore
    static AccountService create(SqlClient dbClient, Handler<AsyncResult<AccountService>> readyHandler) {
        return new PgAccountService(dbClient, readyHandler);
    }

    @GenIgnore
    static AccountService createProxy(Vertx vertx, String eventBusAddress) {
        return new AccountServiceVertxEBProxy(vertx, eventBusAddress);
    }

    @Fluent
    AccountService getAccountByUsernameOrEmail(String UsernameOrEmail, Handler<AsyncResult<Account>> resultHandler);

    @Fluent
    AccountService registerAccount(PreRegistrationAccount preRegistrationAccount, Handler<AsyncResult<Account>> resultHandler);
}
