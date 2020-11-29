package dizzybrawl.http.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dizzybrawl.database.daos.CharacterNioDao;
import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.CharacterMesh;
import dizzybrawl.http.validation.errors.DataErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CharacterApi {

    public Handler<RoutingContext> getAllCharactersByAccountUUID(CharacterNioDao characterNioDao) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            String accountUUID = requestBodyAsJson.getString("account_uuid");

            if (accountUUID == null || accountUUID.isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            try {
                UUID.fromString(accountUUID);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            characterNioDao.getAllByAccountUUID(accountUUID, ar1 -> {
                if (ar1.succeeded()) {
                    List<Character> characters = ar1.result();
                    JsonObject jsonObjectResponse = new JsonObject();
                    JsonArray jsonCharactersResponse = new JsonArray();

                    for (Character character : characters) {
                        JsonObject jsonCharacter = character.toJson();
                        jsonCharacter.remove("account_uuid");
                        jsonCharactersResponse.add(jsonCharacter);
                    }

                    jsonObjectResponse.put("characters", jsonCharactersResponse);

                    context.response().end(jsonObjectResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public Handler<RoutingContext> getAllCharactersMeshesByCharacterUUID(CharacterNioDao characterNioDao) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.getJsonArray("characters") == null || requestBodyAsJson.getJsonArray("characters").isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            List<String> charactersUUIDs = new ArrayList<>();
            try {
                requestBodyAsJson.getJsonArray("characters").stream()
                        .map(o -> ((JsonObject) o).getString("character_uuid"))
                        .distinct()
                        .forEach(uuidString -> {
                            UUID.fromString(uuidString);
                            charactersUUIDs.add(uuidString);
                        });
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            characterNioDao.getAllMeshesByCharacterUUID(charactersUUIDs, ar1 -> {
                if (ar1.succeeded()) {
                    Multimap<String, CharacterMesh> characterUUIDToCharacterMeshesMap = HashMultimap.create();
                    for (CharacterMesh mesh : ar1.result()) {
                        characterUUIDToCharacterMeshesMap.put(mesh.getCharacterUUID().toString(), mesh);
                    }

                    JsonArray jsonCharactersMeshes = new JsonArray();
                    for (String characterUUID : charactersUUIDs) {
                        JsonObject jsonCharacter = new JsonObject();
                        jsonCharacter.put("character_uuid", characterUUID);

                        JsonArray jsonCharacterMeshes = new JsonArray();
                        for (CharacterMesh mesh : characterUUIDToCharacterMeshesMap.get(characterUUID)) {
                            JsonObject jsonMesh = mesh.toJson();
                            jsonMesh.remove("character_uuid");
                            jsonMesh.remove("character_type_id");
                            jsonCharacterMeshes.add(jsonMesh);
                        }

                        jsonCharacter.put("meshes", jsonCharacterMeshes);

                        jsonCharactersMeshes.add(jsonCharacter);
                    }

                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.put("characters", jsonCharactersMeshes);

                    context.response().end(jsonResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
