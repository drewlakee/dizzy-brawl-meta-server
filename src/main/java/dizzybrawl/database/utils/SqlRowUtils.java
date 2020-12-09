package dizzybrawl.database.utils;

import io.vertx.sqlclient.Row;

import java.util.function.Function;

public class SqlRowUtils {

    public static <T> Function<String, T> getElse(Row sqlRow, T elseReturn) {
        return param -> sqlRow.getValue(param) == null ? elseReturn : (T) sqlRow.getValue(param);
    }

    public static <T> Function<String, T> getElse(Row sqlRow, T elseReturn, Class<T> type) {
        return param -> sqlRow.getValue(param) == null ? elseReturn : (T) sqlRow.getValue(param);
    }
}
