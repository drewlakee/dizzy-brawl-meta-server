package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.models.Server;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class ServerMessageCodec implements MessageCodec<Server, Server> {

    @Override
    public void encodeToWire(Buffer buffer, Server server) {
        // TODO
    }

    @Override
    public Server decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public Server transform(Server server) {
        return server;
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
