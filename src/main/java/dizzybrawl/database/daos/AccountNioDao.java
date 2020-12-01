package dizzybrawl.database.daos;

import dizzybrawl.database.models.Account;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNioDao {

    void getByUsernameOrEmail(String usernameOrEmail, Handler<AsyncResult<Account>> resultHandler);

    void register(Account preRegistrationAccount, Handler<AsyncResult<Account>> resultHandler);
}
