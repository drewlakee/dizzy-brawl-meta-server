package dizzybrawl.database.models;

import io.vertx.core.json.JsonObject;

public interface JsonTransformable {

    JsonObject toJson();
}
