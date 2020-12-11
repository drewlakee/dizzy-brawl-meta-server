package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.wrappers.query.executors.AsyncQueryExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class AsyncQueryExecutorMessageCodec implements MessageCodec<AsyncQueryExecutor, AsyncQueryExecutor> {

    @Override
    public void encodeToWire(Buffer buffer, AsyncQueryExecutor asyncQueryExecutor) {
        // TODO
    }

    @Override
    public AsyncQueryExecutor decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public AsyncQueryExecutor transform(AsyncQueryExecutor asyncQueryExecutor) {
        return asyncQueryExecutor;
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
