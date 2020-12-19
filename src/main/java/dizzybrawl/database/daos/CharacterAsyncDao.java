package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import dizzybrawl.database.models.ConcreteWeapon;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterAsyncDao {


    void getAllByAccountID(Vertx vertx, Long accountID, Handler<AsyncResult<List<Character>>> resultHandler);

    void getAllArmorsByAccountID(Vertx vertx, List<Long> charactersIDs, Handler<AsyncResult<List<ConcreteArmor>>> resultHandler);

    void getAllWeaponsByCharactersUUIDs(Vertx vertx, List<Long> charactersIDs, Handler<AsyncResult<List<ConcreteWeapon>>> resultHandler);
}
