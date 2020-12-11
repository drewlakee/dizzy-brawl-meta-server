package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacterNioDao {


    void getAllByAccountUUID(Vertx vertx, UUID accountUUID, Handler<AsyncResult<List<Character>>> resultHandler);

    void getAllArmorsByAccountUUID(Vertx vertx, UUID accountUUID, Handler<AsyncResult<List<ConcreteArmor>>> resultHandler);
}
