package dizzybrawl.http.api;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.CharacterMesh;
import dizzybrawl.database.services.CharacterService;
import dizzybrawl.http.Error;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.UUID;

public class CharacterApi {

    public static Handler<RoutingContext> getAllCharactersByAccountUUID(CharacterService characterService) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.isEmpty()) {
                context.response().end(new JsonObject().put("error", Error.EMPTY_BODY).encodePrettily());
                return;
            }

            String accountUUID = requestBodyAsJson.getString("account_uuid");

            try {
                UUID.fromString(accountUUID);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                return;
            }

            characterService.getAllCharactersByAccountUUID(accountUUID, ar1 -> {
                if (ar1.succeeded()) {
                    List<Character> characters = ar1.result();
                    JsonArray jsonCharactersResponse = new JsonArray();

                    for (Character character : characters) {
                        JsonObject jsonCharacter = character.toJson();
                        jsonCharactersResponse.add(jsonCharacter);
                    }

                    context.response().end(jsonCharactersResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public static Handler<RoutingContext> getAllCharacterMeshesByCharacterUUID(CharacterService characterService) {
        return context -> {
            String characterUUID = context.request().getParam("character_uuid");

            if (characterUUID.isEmpty()) {
                context.response().end(new JsonObject().put("error", Error.EMPTY_QUERY_PARAMETER).encodePrettily());
                return;
            }

            try {
                UUID.fromString(characterUUID);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                return;
            }

            characterService.getAllCharacterMeshesByCharacterUUID(characterUUID, ar1 -> {
                if (ar1.succeeded()) {
                    List<CharacterMesh> characterMeshes = ar1.result();
                    JsonArray jsonCharacterMeshes = new JsonArray();

                    for (CharacterMesh characterMesh : characterMeshes) {
                        jsonCharacterMeshes.add(characterMesh.toJson());
                    }

                    context.response().end(jsonCharacterMeshes.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
