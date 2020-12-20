package dizzybrawl.database.models.format;

import io.vertx.core.json.JsonObject;

public interface JsonTransformable {

    JsonObject toJson();
}
