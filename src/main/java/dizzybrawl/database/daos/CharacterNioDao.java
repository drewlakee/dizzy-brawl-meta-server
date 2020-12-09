package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacterNioDao {


    void getAllByAccountUUID(UUID accountUUID, Handler<AsyncResult<List<Character>>> resultHandler);

    void getAllArmorsByAccountUUID(UUID accountUUID, Handler<AsyncResult<List<ConcreteArmor>>> resultHandler);
}
