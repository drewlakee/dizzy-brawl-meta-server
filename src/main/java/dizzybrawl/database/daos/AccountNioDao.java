package dizzybrawl.database.daos;

import dizzybrawl.database.models.VerifiedAccount;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNioDao {

    void getByUsernameOrEmail(String usernameOrEmail, Handler<AsyncResult<VerifiedAccount>> resultHandler);
}
