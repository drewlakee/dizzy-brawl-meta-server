package dizzybrawl.http.validation;

import dizzybrawl.http.validation.errors.JsonErrors;
import io.vertx.ext.web.RoutingContext;

public class JsonObjectValidationHandler implements ValidationHandler {

    public static ValidationHandler create() {
        return new JsonObjectValidationHandler();
    }

    @Override
    public void handle(RoutingContext event) {
        try {
            event.getBodyAsJson();
        } catch (Exception e) {
            event.response()
                    .end(
                            toJsonErrorResponse(JsonErrors.EMPTY_BODY.name(), "Json body is empty").encodePrettily()
                    );
            return;
        }

        if (event.getBodyAsJson().isEmpty()) {
            event.response()
                    .end(
                            toJsonErrorResponse(JsonErrors.EMPTY_BODY.name(), "Json body is empty").encodePrettily()
                    );
            return;
        }

        event.next();
    }
}
