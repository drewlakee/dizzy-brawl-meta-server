package dizzybrawl.verticles.eventBus;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class EventBusObjectWrapperMessageCodec implements MessageCodec<EventBusObjectWrapper, EventBusObjectWrapper> {

    @Override
    public void encodeToWire(Buffer buffer, EventBusObjectWrapper eventBusObjectWrapper) {}

    @Override
    public EventBusObjectWrapper decodeFromWire(int pos, Buffer buffer) { return null; }

    @Override
    public EventBusObjectWrapper transform(EventBusObjectWrapper eventBusObjectWrapper) {
        return eventBusObjectWrapper;
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
