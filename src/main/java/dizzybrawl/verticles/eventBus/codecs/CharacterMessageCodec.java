package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.models.Character;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class CharacterMessageCodec implements MessageCodec<Character, Character> {

    @Override
    public void encodeToWire(Buffer buffer, Character character) {
        // TODO
    }

    @Override
    public Character decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public Character transform(Character character) {
        return character;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
