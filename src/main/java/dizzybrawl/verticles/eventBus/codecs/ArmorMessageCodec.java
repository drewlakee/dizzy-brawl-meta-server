package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.models.Armor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class ArmorMessageCodec implements MessageCodec<Armor, Armor> {

    @Override
    public void encodeToWire(Buffer buffer, Armor armor) {
        // TODO
    }

    @Override
    public Armor decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public Armor transform(Armor armor) {
        return armor;
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
