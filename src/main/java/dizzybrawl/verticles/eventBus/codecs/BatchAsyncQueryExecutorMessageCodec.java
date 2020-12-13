package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.wrappers.query.executors.BatchAsyncQueryExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class BatchAsyncQueryExecutorMessageCodec implements MessageCodec<BatchAsyncQueryExecutor, BatchAsyncQueryExecutor> {

    @Override
    public void encodeToWire(Buffer buffer, BatchAsyncQueryExecutor batchAsyncQueryExecutor) {
        // TODO
    }

    @Override
    public BatchAsyncQueryExecutor decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public BatchAsyncQueryExecutor transform(BatchAsyncQueryExecutor batchAsyncQueryExecutor) {
        return batchAsyncQueryExecutor;
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
