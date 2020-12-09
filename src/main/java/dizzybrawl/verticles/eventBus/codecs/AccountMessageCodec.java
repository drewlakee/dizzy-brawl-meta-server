package dizzybrawl.verticles.eventBus.codecs;

import dizzybrawl.database.models.Account;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class AccountMessageCodec implements MessageCodec<Account, Account> {

    @Override
    public void encodeToWire(Buffer buffer, Account account) {
        // TODO
    }

    @Override
    public Account decodeFromWire(int pos, Buffer buffer) {
        // TODO
        return null;
    }

    @Override
    public Account transform(Account account) {
        return account;
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
