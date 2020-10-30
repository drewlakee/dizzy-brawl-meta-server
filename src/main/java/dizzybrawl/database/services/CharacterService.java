package dizzybrawl.database.services;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.CharacterMesh;
import dizzybrawl.database.services.impls.PgCharacterService;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.SqlClient;

import java.util.List;

@ProxyGen
@VertxGen
public interface CharacterService {

    @GenIgnore
    static CharacterService create(SqlClient dbClient, Handler<AsyncResult<CharacterService>> readyHandler) {
        return new PgCharacterService(dbClient, readyHandler);
    }

    @GenIgnore
    static CharacterService createProxy(Vertx vertx, String eventBusAddress) {
        return new CharacterServiceVertxEBProxy(vertx, eventBusAddress);
    }

    @Fluent
    CharacterService getAllCharactersByAccountUUID(String accountUUID, Handler<AsyncResult<List<Character>>> resultHandler);

    @Fluent
    CharacterService getAllCharacterMeshesByCharacterUUID(List<String> characterUUIDs, Handler<AsyncResult<List<CharacterMesh>>> resultHandler);
}
