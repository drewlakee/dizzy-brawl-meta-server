package dizzybrawl.database.wrappers.query.executors;

import io.vertx.sqlclient.SqlConnection;

public class AsyncQueryExecutor extends AbstractQueryExecutor implements Executor {

    public AsyncQueryExecutor(String sqlQuery) {
        super(sqlQuery);
    }

    @Override
    public void execute(SqlConnection connection) {
        this.connection = connection;
        this.connection.query(sqlQuery).execute(handler);
    }
}
