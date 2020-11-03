package dizzybrawl.http.validation;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public interface ValidationHandler extends Handler<RoutingContext> {

    default JsonObject toJsonErrorResponse(String error, String message) {
        return new JsonObject()
                .put("error", error)
                .put("message", message);
    }
}
