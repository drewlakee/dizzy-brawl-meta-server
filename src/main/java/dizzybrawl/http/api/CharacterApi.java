package dizzybrawl.http.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dizzybrawl.database.daos.CharacterNioDao;
import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.Armor;
import dizzybrawl.database.models.ConcreteArmor;
import dizzybrawl.http.validation.errors.DataErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CharacterApi {

    public Handler<RoutingContext> getAllCharactersByAccountUUIDHandler(CharacterNioDao characterNioDao) {
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

    public Handler<RoutingContext> getAllArmorsByAccountsUUIDsHandler(CharacterNioDao characterNioDao) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            UUID accountUUID;
            try {
                accountUUID = UUID.fromString(requestBodyAsJson.getString("account_uuid"));
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            characterNioDao.getAllArmorsByAccountUUID(accountUUID, ar1 -> {
                if (ar1.succeeded()) {
                    JsonArray jsonArmors = new JsonArray();
                    ar1.result().forEach(armor -> {
                        JsonObject jsonArmor = armor.toJson();
                        jsonArmor.remove("account_uuid");
                        jsonArmors.add(jsonArmor);
                    });
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.put("armors", jsonArmors);
                    context.response().end(jsonResponse.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
