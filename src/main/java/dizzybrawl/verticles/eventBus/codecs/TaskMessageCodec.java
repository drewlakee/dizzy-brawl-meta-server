package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.models.Task;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class TaskMessageCodec implements MessageCodec<Task, Task> {

    @Override
    public void encodeToWire(Buffer buffer, Task task) {
        // TODO
    }

    @Override
    public Task decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public Task transform(Task task) {
        return task;
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
