package dizzybrawl.http.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dizzybrawl.database.models.Character;
import dizzybrawl.database.models.*;
import dizzybrawl.http.validation.errors.DataErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import dizzybrawl.verticles.CharacterServiceVerticle;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CharacterApi {

    public Handler<RoutingContext> onGetAllCharacters(Vertx vertx) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.getLong(Account.ACCOUNT_ID) == null) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            Long accountID;
            try {
                accountID = requestBodyAsJson.getLong(Account.ACCOUNT_ID);
            } catch (ClassCastException e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_ID).encodePrettily());
                return;
            }

            if (accountID < 1) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_ID).encodePrettily());
                return;
            }

            vertx.eventBus().<EventBusObjectWrapper<List<Character>>>request(CharacterServiceVerticle.GET_ALL_ADDRESS, EventBusObjectWrapper.of(accountID), ar1 -> {
                if (ar1.succeeded()) {
                    List<Character> characters = ar1.result().body().get();
                    JsonObject jsonObjectResponse = new JsonObject();
                    JsonArray jsonCharactersResponse = new JsonArray();

                    for (Character character : characters) {
                        JsonObject jsonCharacter = character.toJson();
                        jsonCharacter.remove(Account.ACCOUNT_ID);
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
            JsonObject characters = context.getBodyAsJson();

            List<Long> charactersIDs = new ArrayList<>();
            try {
                characters.getJsonArray("characters").stream()
                        .map(o -> (JsonObject) o)
                        .map(jo -> jo.getLong("character_id"))
                        .forEach(charactersIDs::add);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_ID).encodePrettily());
                return;
            }

            charactersIDs.forEach(id -> {
                if (id < 1) {
                    context.response().end(new JsonObject().put("error", DataErrors.INVALID_ID).encodePrettily());
                    return;
                }
            });

            if (charactersIDs.isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            vertx.eventBus().<EventBusObjectWrapper<List<ConcreteArmor>>>request(CharacterServiceVerticle.GET_ALL_ARMORS_ADDRESS, EventBusObjectWrapper.of(charactersIDs), ar1 -> {
                if (ar1.succeeded()) {
                    List<ConcreteArmor> concreteArmors = ar1.result().body().get();
                    Multimap<Long, ConcreteArmor> characterToArmors = ArrayListMultimap.create();

                    for (ConcreteArmor concreteArmor : concreteArmors) {
                        characterToArmors.put(concreteArmor.getCharacterID(), concreteArmor);
                    }

                    JsonArray allRequestedWeapons = new JsonArray();
                    for (Long characterID : charactersIDs) {
                        Collection<ConcreteArmor> weaponsOfConcreteCharacter = characterToArmors.get(characterID);

                        JsonArray jsonArrayOfWeaponsForConcreteCharacter = new JsonArray();
                        for (ConcreteArmor concreteArmor : weaponsOfConcreteCharacter) {
                            JsonObject jsonWeapon = concreteArmor.toJson();
                            jsonWeapon.remove(Character.CHARACTER_ID);
                            jsonArrayOfWeaponsForConcreteCharacter.add(jsonWeapon);
                        }

                        allRequestedWeapons.add(jsonArrayOfWeaponsForConcreteCharacter);
                    }

                    JsonObject response = new JsonObject();
                    response.put("armors", allRequestedWeapons);

                    context.response().end(response.encodePrettily());
                } else {
                    context.fail(ar1.cause());
                }
            });
        };
    }

    public Handler<RoutingContext> onGetAllWeapons(Vertx vertx) {
        return context -> {
            JsonObject characters = context.getBodyAsJson();

            List<Long> charactersIDs = new ArrayList<>();
            try {
                characters.getJsonArray("characters").stream()
                        .map(o -> (JsonObject) o)
                        .map(jo -> jo.getLong("character_id"))
                        .forEach(charactersIDs::add);
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_ID).encodePrettily());
                return;
            }

            charactersIDs.forEach(id -> {
                if (id < 1) {
                    context.response().end(new JsonObject().put("error", DataErrors.INVALID_ID).encodePrettily());
                    return;
                }
            });

            if (charactersIDs.isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            vertx.eventBus().<EventBusObjectWrapper<List<ConcreteWeapon>>>request(CharacterServiceVerticle.GET_ALL_WEAPONS_ADDRESS, EventBusObjectWrapper.of(charactersIDs), ar1 -> {
               if (ar1.succeeded()) {
                   List<ConcreteWeapon> concreteWeapons = ar1.result().body().get();
                   Multimap<Long, ConcreteWeapon> characterToWeapons = ArrayListMultimap.create();

                   for (ConcreteWeapon concreteWeapon : concreteWeapons) {
                       characterToWeapons.put(concreteWeapon.getCharacterID(), concreteWeapon);
                   }

                   JsonArray allRequestedWeapons = new JsonArray();
                   for (Long characterID : charactersIDs) {
                       Collection<ConcreteWeapon> weaponsOfConcreteCharacter = characterToWeapons.get(characterID);

                       JsonArray jsonArrayOfWeaponsForConcreteCharacter = new JsonArray();
                       for (ConcreteWeapon concreteWeapon : weaponsOfConcreteCharacter) {
                           JsonObject jsonWeapon = concreteWeapon.toJson();
                           jsonWeapon.remove(CharacterType.CHARACTER_TYPE_ID);
                           jsonWeapon.remove(Character.CHARACTER_ID);
                           jsonArrayOfWeaponsForConcreteCharacter.add(jsonWeapon);
                       }

                       allRequestedWeapons.add(jsonArrayOfWeaponsForConcreteCharacter);
                   }

                   JsonObject response = new JsonObject();
                   response.put("weapons", allRequestedWeapons);

                   context.response().end(response.encodePrettily());
               } else {
                   context.fail(ar1.cause());
               }
            });
        };
    }
}
