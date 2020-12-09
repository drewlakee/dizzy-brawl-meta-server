package dizzybrawl.http.utils;

import io.vertx.core.json.JsonObject;

import java.util.function.Function;

public class JsonUtils {

    public static <T> Function<String, T> getElse(JsonObject jsonObject, T elseReturn) {
        return param -> jsonObject.containsKey(param) ? (T) jsonObject.getValue(param) : elseReturn;
    }

    public static <T> Function<String, T> getElse(JsonObject jsonObject, T elseReturn, Class<T> type) {
        return param -> jsonObject.containsKey(param) ? (T) jsonObject.getValue(param) : elseReturn;
    }

}
