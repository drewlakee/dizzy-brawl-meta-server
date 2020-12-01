package dizzybrawl.database.daos;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteCharacterMesh;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterNioDao {


    void getAllByAccountUUID(String accountUUID, Handler<AsyncResult<List<Character>>> resultHandler);

    void getAllMeshesByCharacterUUID(List<String> charactersUUIDs, Handler<AsyncResult<List<ConcreteCharacterMesh>>> resultHandler);
}
