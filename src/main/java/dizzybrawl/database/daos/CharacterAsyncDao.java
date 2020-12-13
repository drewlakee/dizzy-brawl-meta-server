package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import dizzybrawl.database.models.ConcreteWeapon;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacterAsyncDao {


    void getAllByAccountUUID(Vertx vertx, UUID accountUUID, Handler<AsyncResult<List<Character>>> resultHandler);

    void getAllArmorsByAccountUUID(Vertx vertx, UUID accountUUID, Handler<AsyncResult<List<ConcreteArmor>>> resultHandler);

    void getAllWeaponsByCharactersUUIDs(Vertx vertx, List<UUID> charactersUUIDs, Handler<AsyncResult<List<ConcreteWeapon>>> resultHandler);
}
