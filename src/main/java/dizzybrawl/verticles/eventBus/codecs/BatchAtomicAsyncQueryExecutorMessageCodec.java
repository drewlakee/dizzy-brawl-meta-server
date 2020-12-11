package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.wrappers.query.executors.BatchAtomicAsyncQueryExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class BatchAtomicAsyncQueryExecutorMessageCodec implements MessageCodec<BatchAtomicAsyncQueryExecutor, BatchAtomicAsyncQueryExecutor> {

    @Override
    public void encodeToWire(Buffer buffer, BatchAtomicAsyncQueryExecutor batchAtomicAsyncQueryExecutor) {
        // TODO
    }

    @Override
    public BatchAtomicAsyncQueryExecutor decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public BatchAtomicAsyncQueryExecutor transform(BatchAtomicAsyncQueryExecutor batchAtomicAsyncQueryExecutor) {
        return batchAtomicAsyncQueryExecutor;
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
