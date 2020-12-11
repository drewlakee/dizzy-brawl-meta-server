package dizzybrawl.database.wrappers.query.executors;

import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.Objects;

public class BatchAtomicAsyncQueryExecutor extends AtomicAsyncQueryExecutor implements Executor {

    private final List<Tuple> batch;

    public BatchAtomicAsyncQueryExecutor(String sqlQuery, List<Tuple> batch) {
        super(sqlQuery);
        this.batch = batch;
    }

    @Override
    public void execute(SqlConnection connection) {
        this.connection = connection;
        this.transaction = connection.begin();

        transaction.preparedQuery(sqlQuery).executeBatch(batch, handler);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BatchAtomicAsyncQueryExecutor that = (BatchAtomicAsyncQueryExecutor) o;
        return Objects.equals(batch, that.batch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), batch);
    }
}
