package dizzybrawl.database.sql;

import java.util.HashMap;

public interface SqlLoadable<T> {

    HashMap<T, String> loadSqlQueries();
}
