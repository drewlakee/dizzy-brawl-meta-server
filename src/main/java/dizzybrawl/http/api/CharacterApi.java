package dizzybrawl.http.api;

import dizzybrawl.database.models.Character;
import dizzybrawl.database.services.CharacterService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.UUID;

public class CharacterApi {

    public static Handler<RoutingContext> getAllCharactersByAccountUUID(CharacterService characterService) {
        return context -> {
            String accountUUID = context.request().getParam("account_uuid");

            try {
                UUID.fromString(accountUUID);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", "Wrong account uuid format").encodePrettily());
                return;
            }

            characterService.getAllCharactersByAccountUUID(accountUUID, ar1 -> {
                if (ar1.succeeded()) {
                    List<Character> characters = ar1.result();
                    JsonArray jsonCharactersResponse = new JsonArray();

                    for (Character character : characters) {
                        JsonObject jsonCharacter = character.toJson();
                        jsonCharacter.remove("account_uuid");
                        jsonCharactersResponse.add(jsonCharacter);
                    }

                    context.response().end(jsonCharactersResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
