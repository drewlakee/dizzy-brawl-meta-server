package dizzybrawl.database.daos;

import dizzybrawl.database.models.Account;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNioDao {

    void getByUsernameOrEmail(Vertx vertx, String usernameOrEmail, Handler<AsyncResult<Account>> resultHandler);

    void register(Vertx vertx, Account preRegistrationAccount, Handler<AsyncResult<Account>> resultHandler);
}
