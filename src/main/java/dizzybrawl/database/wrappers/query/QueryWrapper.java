package dizzybrawl.database.wrappers.query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.util.Objects;

public class QueryWrapper {

    protected final String sqlQuery;
    protected Handler<AsyncResult<RowSet<Row>>> handler;

    public QueryWrapper(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        this.handler = emptyHandler -> {};
    }

    public void setHandler(Handler<AsyncResult<RowSet<Row>>> handler) {
        this.handler = handler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryWrapper that = (QueryWrapper) o;
        return Objects.equals(sqlQuery, that.sqlQuery) && Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sqlQuery, handler);
    }
}
