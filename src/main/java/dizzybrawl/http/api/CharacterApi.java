package dizzybrawl.http.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.ConcreteArmor;
import dizzybrawl.database.models.ConcreteWeapon;
import dizzybrawl.http.validation.errors.DataErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import dizzybrawl.verticles.CharacterServiceVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class CharacterApi {

    public Handler<RoutingContext> onGetAllCharacters(Vertx vertx) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            UUID accountUUID;

            if (requestBodyAsJson.getString("account_uuid") == null || requestBodyAsJson.getString("account_uuid").isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            try {
                accountUUID = UUID.fromString(requestBodyAsJson.getString("account_uuid"));
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            vertx.eventBus().<List<Character>>request(CharacterServiceVerticle.GET_ALL_ADDRESS, accountUUID, ar1 -> {
                if (ar1.succeeded()) {
                    List<Character> characters = ar1.result().body();
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

    public Handler<RoutingContext> onGetAllArmors(Vertx vertx) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            UUID accountUUID;
            try {
                accountUUID = UUID.fromString(requestBodyAsJson.getString("account_uuid"));
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            vertx.eventBus().<List<ConcreteArmor>>request(CharacterServiceVerticle.GET_ALL_ARMORS_ADDRESS, accountUUID, ar1 -> {
                if (ar1.succeeded()) {
                    JsonArray jsonArmors = new JsonArray();
                    ar1.result().body().forEach(armor -> {
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

    public Handler<RoutingContext> onGetAllWeapons(Vertx vertx) {
        return context -> {
            JsonObject characters = context.getBodyAsJson();

            List<UUID> charactersUUIDs = new ArrayList<>();
            try {
                characters.getJsonArray("characters").stream()
                        .map(o -> (JsonObject) o)
                        .map(jo -> UUID.fromString(jo.getString("character_uuid")))
                        .distinct()
                        .forEach(charactersUUIDs::add);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            if (charactersUUIDs.isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            vertx.eventBus().<List<ConcreteWeapon>>request(CharacterServiceVerticle.GET_ALL_WEAPONS_ADDRESS, charactersUUIDs, ar1 -> {
               if (ar1.succeeded()) {
                   List<ConcreteWeapon> concreteWeapons = ar1.result().body();
                   Multimap<String, ConcreteWeapon> characterToWeapons = ArrayListMultimap.create();

                   for (ConcreteWeapon concreteWeapon : concreteWeapons) {
                       characterToWeapons.put(concreteWeapon.getCharacterUUID().toString(), concreteWeapon);
                   }

                   JsonArray allRequestedWeapons = new JsonArray();
                   for (UUID characterUUID : charactersUUIDs) {
                       Collection<ConcreteWeapon> weaponsOfConcreteCharacter = characterToWeapons.get(characterUUID.toString());

                       JsonArray jsonArrayOfWeaponsForConcreteCharacter = new JsonArray();
                       for (ConcreteWeapon concreteWeapon : weaponsOfConcreteCharacter) {
                           JsonObject jsonWeapon = concreteWeapon.toJson();
                           jsonWeapon.remove("character_type_id");
                           jsonWeapon.remove("character_uuid");
                           jsonArrayOfWeaponsForConcreteCharacter.add(jsonWeapon);
                       }

                       allRequestedWeapons.add(jsonArrayOfWeaponsForConcreteCharacter);
                   }

                   JsonObject response = new JsonObject();
                   response.put("characters", allRequestedWeapons);

                   context.response().end(response.encodePrettily());
               } else {
                   context.fail(ar1.cause());
               }
            });
        };
    }
}
