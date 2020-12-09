package dizzybrawl.verticles.eventBus.codecs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.util.ArrayList;

public class ArrayListMessageCodec implements MessageCodec<ArrayList, ArrayList> {

    @Override
    public void encodeToWire(Buffer buffer, ArrayList arrayList) {
        // TODO
    }

    @Override
    public ArrayList decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public ArrayList transform(ArrayList arrayList) {
        return arrayList;
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
