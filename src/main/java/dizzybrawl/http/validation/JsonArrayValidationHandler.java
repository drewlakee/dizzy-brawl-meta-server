package dizzybrawl.http.validation;

import dizzybrawl.http.validation.errors.JsonErrors;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class JsonArrayValidationHandler implements ValidationHandler {

    public static ValidationHandler create() {
        return new JsonArrayValidationHandler();
    }

    @Override
    public void handle(RoutingContext event) {
        try {
            event.getBodyAsJsonArray();
        } catch (Exception e) {
            event.response()
                    .end(toJsonErrorResponse(JsonErrors.EMPTY_BODY.name(), "Json query body is empty").encodePrettily());
            return;
        }

        if (event.getBodyAsJsonArray().isEmpty()) {
            event.response()
                    .end(toJsonErrorResponse(JsonErrors.EMPTY_BODY.name(), "Json query body is empty").encodePrettily());
            return;
        }

        for (int position = 0; position < event.getBodyAsJsonArray().size(); position++) {
            JsonObject jsonObject = event.getBodyAsJsonArray().getJsonObject(position);

            if (jsonObject.isEmpty()) {
                event.response()
                        .end(toJsonErrorResponse(JsonErrors.EMPTY_JSON_PARAMETERS.name(), "Json parameters is empty").encodePrettily());
                return;
            }
        }

        event.next();
    }
}
