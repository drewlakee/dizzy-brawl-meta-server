package dizzybrawl.database.wrappers.query.executors;

import dizzybrawl.database.wrappers.query.QueryWrapper;
import io.vertx.sqlclient.SqlConnection;

import java.util.Objects;

public abstract class AbstractQueryExecutor extends QueryWrapper implements Executor {

    protected SqlConnection connection;

    public AbstractQueryExecutor(String sqlQuery) {
        super(sqlQuery);
    }

    public void releaseConnection() {
        this.connection.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractQueryExecutor that = (AbstractQueryExecutor) o;
        return Objects.equals(connection, that.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), connection);
    }
}
