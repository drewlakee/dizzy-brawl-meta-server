package dizzybrawl.verticles;

import dizzybrawl.database.daos.CharacterAsyncDao;
import dizzybrawl.verticles.eventBus.EventBusObjectWrapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CharacterServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS = "character.service";
    public static final String GET_ALL_ADDRESS = ADDRESS + ".get.all";
    public static final String GET_ALL_ARMORS_ADDRESS = ADDRESS + ".armors.get.all";
    public static final String GET_ALL_WEAPONS_ADDRESS = ADDRESS + ".weapons.get.all";

    private final CharacterAsyncDao characterAsyncDao;

    @Autowired
    public CharacterServiceVerticle(CharacterAsyncDao characterAsyncDao) {
        this.characterAsyncDao = characterAsyncDao;
    }

    @Override
    public void start(Promise<Void> startPromise) {

        vertx.eventBus().<EventBusObjectWrapper<Long>>consumer(GET_ALL_ADDRESS, handler -> {
            characterAsyncDao.getAllByAccountID(vertx, handler.body().get(), ar1 -> {
                if (ar1.succeeded()) {
                    handler.reply(EventBusObjectWrapper.of(ar1.result()));
                }
            });
        });

        vertx.eventBus().<EventBusObjectWrapper<List<Long>>>consumer(GET_ALL_ARMORS_ADDRESS, handler -> {
           characterAsyncDao.getAllArmorsByAccountID(vertx, handler.body().get(), ar1 -> {
               if (ar1.succeeded()) {
                   handler.reply(EventBusObjectWrapper.of(ar1.result()));
               }
           });
        });

        vertx.eventBus().<EventBusObjectWrapper<List<Long>>>consumer(GET_ALL_WEAPONS_ADDRESS, handler -> {
           characterAsyncDao.getAllWeaponsByCharactersUUIDs(vertx, handler.body().get(), ar1 -> {
               if (ar1.succeeded()) {
                   handler.reply(EventBusObjectWrapper.of(ar1.result()));
               }
           });
        });

        startPromise.complete();
    }
}
