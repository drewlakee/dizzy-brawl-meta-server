package dizzybrawl.http.api;

import dizzybrawl.database.models.Character;
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
        return context -> context.vertx().<RoutingContext>executeBlocking(future -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.isEmpty()) {
                context.response().write(new JsonObject().put("error", Error.EMPTY_BODY).encodePrettily());
                future.complete(context);
                return;
            }

            String accountUUID = requestBodyAsJson.getString("account_uuid");

            try {
                UUID.fromString(accountUUID);
            } catch (Exception e) {
                context.response().write(new JsonObject().put("error", Error.INVALID_QUERY_PARAMETER_FORMAT).encodePrettily());
                future.complete(context);
                return;
            }

            characterService.getAllCharactersByAccountUUID(accountUUID, ar2 -> {
                if (ar2.succeeded()) {
                    List<Character> characters = ar2.result();
                    JsonArray jsonCharactersResponse = new JsonArray();

                    for (Character character : characters) {
                        JsonObject jsonCharacter = character.toJson();
                        jsonCharactersResponse.add(jsonCharacter);
                    }

                    context.response().write(jsonCharactersResponse.encodePrettily());

                    future.complete(context);
                } else {
                    future.fail(ar2.cause());
                }
            });
        }, ar1 -> {
            if (ar1.succeeded()) {
                ar1.result().response().end();
            } else {
                ar1.result().fail(ar1.cause());
            }
        });
    }
}
