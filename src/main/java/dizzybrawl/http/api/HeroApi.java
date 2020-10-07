package dizzybrawl.http.api;

import dizzybrawl.database.services.HeroService;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class HeroApi {

    public static Handler<RoutingContext> GetHeroByIdHandler(HeroService heroService) {
        return context -> {
            int heroId = Integer.parseInt(context.request().getParam("id"));
            heroService.getHeroById(heroId, ar1 -> {
                if (ar1.succeeded()) {
                    context.response().end(ar1.result().encodePrettily());
                }  else {
                    context.fail(ar1.cause());
                }
            });
        };
    }
}
