package dizzybrawl.database.wrappers.query.executors;

import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;

import java.util.Objects;

public class AtomicAsyncQueryExecutor extends AbstractQueryExecutor implements Executor {

    protected Transaction transaction;

    public AtomicAsyncQueryExecutor(String sqlQuery) {
        super(sqlQuery);
    }

    @Override
    public void execute(SqlConnection connection) {
        this.connection = connection;
        this.transaction = connection.begin();

        this.transaction.query(sqlQuery).execute(handler);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AtomicAsyncQueryExecutor that = (AtomicAsyncQueryExecutor) o;
        return Objects.equals(transaction, that.transaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transaction);
    }
}
