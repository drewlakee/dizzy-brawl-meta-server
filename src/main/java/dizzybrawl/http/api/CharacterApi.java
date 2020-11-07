package dizzybrawl.http.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.CharacterMesh;
import dizzybrawl.database.services.CharacterService;
import dizzybrawl.http.validation.CommonErrors;
import dizzybrawl.http.validation.JsonErrors;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CharacterApi {

    public static Handler<RoutingContext> getAllCharactersByAccountUUID(CharacterService characterService) {
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
                context.response().end(new JsonObject().put("error", CommonErrors.INVALID_UUID).encodePrettily());
                return;
            }

            characterService.getAllCharactersByAccountUUID(accountUUID, ar1 -> {
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

    public static Handler<RoutingContext> getAllCharacterMeshesByCharacterUUID(CharacterService characterService) {
        return context -> {
            JsonArray requestBodyAsJsonArray = context.getBodyAsJsonArray();

            List<String> characterUUIDs = new ArrayList<>();
            try {
                requestBodyAsJsonArray.stream()
                        .map(o -> ((JsonObject) o).getString("character_uuid"))
                        .distinct()
                        .forEach(uuidString -> {
                            UUID.fromString(uuidString);
                            characterUUIDs.add(uuidString);
                        });
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", CommonErrors.INVALID_UUID).encodePrettily());
                return;
            }

            characterService.getAllCharacterMeshesByCharacterUUID(characterUUIDs, ar1 -> {
                if (ar1.succeeded()) {
                    Multimap<String, CharacterMesh> characterUUIDToCharacterMeshesMap = HashMultimap.create();
                    for (CharacterMesh mesh : ar1.result()) {
                        characterUUIDToCharacterMeshesMap.put(mesh.getCharacterUUID().toString(), mesh);
                    }

                    JsonArray jsonResponse = new JsonArray();
                    for (String characterUUID : characterUUIDs) {
                        JsonObject jsonCharacter = new JsonObject();
                        jsonCharacter.put("character_uuid", characterUUID);

                        JsonArray jsonCharacterMeshes = new JsonArray();
                        for (CharacterMesh mesh : characterUUIDToCharacterMeshesMap.get(characterUUID)) {
                            JsonObject jsonMesh = mesh.toJson();
                            jsonMesh.remove("character_uuid");
                            jsonMesh.remove("character_type_id");
                            jsonCharacterMeshes.add(jsonMesh);
                        }

                        jsonCharacter.put("character_meshes", jsonCharacterMeshes);

                        jsonResponse.add(jsonCharacter);
                    }

                    context.response().end(jsonResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
