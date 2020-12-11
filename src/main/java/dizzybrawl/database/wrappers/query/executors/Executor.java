package dizzybrawl.database.wrappers.query.executors;

import io.vertx.sqlclient.SqlConnection;

@FunctionalInterface
public interface Executor {

    void execute(SqlConnection connection);
}
