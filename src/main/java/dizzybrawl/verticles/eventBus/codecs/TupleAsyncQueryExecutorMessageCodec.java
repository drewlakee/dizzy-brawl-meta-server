package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.wrappers.query.executors.TupleAsyncQueryExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class TupleAsyncQueryExecutorMessageCodec implements MessageCodec<TupleAsyncQueryExecutor, TupleAsyncQueryExecutor> {

    @Override
    public void encodeToWire(Buffer buffer, TupleAsyncQueryExecutor tupleAsyncQueryExecutor) {
        // TODO
    }

    @Override
    public TupleAsyncQueryExecutor decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public TupleAsyncQueryExecutor transform(TupleAsyncQueryExecutor tupleAsyncQueryExecutor) {
        return tupleAsyncQueryExecutor;
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
