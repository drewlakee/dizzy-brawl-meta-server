package dizzybrawl.verticles.eventBus.codecs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.util.UUID;

public class UUIDMessageCodec implements MessageCodec<UUID, UUID> {

    @Override
    public void encodeToWire(Buffer buffer, UUID uuid) {
        // TODO
    }

    @Override
    public UUID decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public UUID transform(UUID uuid) {
        return uuid;
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
