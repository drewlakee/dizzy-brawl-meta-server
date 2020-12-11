package dizzybrawl.verticles;

import dizzybrawl.database.daos.CharacterAsyncDao;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CharacterServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS = "character.service";
    public static final String GET_ALL_ADDRESS = ADDRESS + ".get.all";
    public static final String GET_ALL_ARMORS_ADDRESS = ADDRESS + ".armors.get.all";

    private final CharacterAsyncDao characterAsyncDao;

    @Autowired
    public CharacterServiceVerticle(CharacterAsyncDao characterAsyncDao) {
        this.characterAsyncDao = characterAsyncDao;
    }

    @Override
    public void start(Promise<Void> startPromise) {

        vertx.eventBus().<UUID>consumer(GET_ALL_ADDRESS, handler -> {
            characterAsyncDao.getAllByAccountUUID(vertx, handler.body(), ar1 -> {
                if (ar1.succeeded()) {
                    handler.reply(ar1.result());
                }
            });
        });

        vertx.eventBus().<UUID>consumer(GET_ALL_ARMORS_ADDRESS, handler -> {
           characterAsyncDao.getAllArmorsByAccountUUID(vertx, handler.body(), ar1 -> {
               if (ar1.succeeded()) {
                   handler.reply(ar1.result());
               }
           });
        });

        startPromise.complete();
    }
}
