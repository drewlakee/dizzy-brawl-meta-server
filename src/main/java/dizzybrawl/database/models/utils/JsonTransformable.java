package dizzybrawl.database.models.utils;

import io.vertx.core.json.JsonObject;

public interface JsonTransformable {

    JsonObject toJson();
}
