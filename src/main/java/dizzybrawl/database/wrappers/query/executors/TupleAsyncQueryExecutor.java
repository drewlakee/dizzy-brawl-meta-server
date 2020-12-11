package dizzybrawl.database.wrappers.query.executors;

import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.Objects;

public class TupleAsyncQueryExecutor extends AsyncQueryExecutor implements Executor {

    protected final Tuple tuple;

    public TupleAsyncQueryExecutor(String sqlQuery, Tuple tuple) {
        super(sqlQuery);
        this.tuple = tuple;
    }

    @Override
    public void execute(SqlConnection connection) {
        this.connection = connection;

        connection.preparedQuery(sqlQuery).execute(tuple, handler);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TupleAsyncQueryExecutor that = (TupleAsyncQueryExecutor) o;
        return Objects.equals(tuple, that.tuple);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tuple);
    }
}
