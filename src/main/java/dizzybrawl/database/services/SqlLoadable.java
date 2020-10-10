package dizzybrawl.database.services;

import java.util.HashMap;

public interface SqlLoadable<T> {

    HashMap<T, String> loadSqlQueries();
}
