package dizzybrawl.http.api;

import dizzybrawl.database.daos.ServerNioDao;
import dizzybrawl.database.models.Server;
import dizzybrawl.http.validation.errors.DataErrors;
import dizzybrawl.http.validation.errors.DatabaseErrors;
import dizzybrawl.http.validation.errors.JsonErrors;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ServerApi {

    public Handler<RoutingContext> getAllHandler(ServerNioDao serverNioDao) {
        return context -> {
              serverNioDao.getAll(ar1 -> {
                  if (ar1.succeeded()) {
                      List<JsonObject> servers = ar1.result().stream().map(Server::toJson).collect(Collectors.toList());
                      JsonObject response = new JsonObject();
                      JsonArray array = new JsonArray(servers);
                      response.put("servers", array);
                      context.response().end(response.encodePrettily());
                  }
              });
        };
    }

    public Handler<RoutingContext> addHandler(ServerNioDao serverNioDao) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.getJsonArray("servers") == null || requestBodyAsJson.getJsonArray("servers").isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            List<Server> serversToAdd = new ArrayList<>();
            try {
                requestBodyAsJson.getJsonArray("servers").stream()
                        .map(object -> (JsonObject) object)
                        .forEach(jsonObject -> serversToAdd.add(new Server(jsonObject)));
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            serverNioDao.add(serversToAdd, ar1 -> {
                if (ar1.succeeded()) {
                    List<Server> addedServers = ar1.result();
                    JsonObject response = new JsonObject();
                    JsonArray array = new JsonArray();
                    for (Server addedServer : addedServers) {
                        array.add(new JsonObject().put("server_uuid", addedServer.getServerUUID().toString()));
                    }
                    response.put("servers", array);

                    context.response().end(response.encodePrettily());
                } else {
                    context.response().end(new JsonObject().put("error", DatabaseErrors.ALREADY_EXIST_AT_DATABASE).encodePrettily());
                }
            });
        };
    }

    public Handler<RoutingContext> deleteHandler(ServerNioDao serverNioDao) {
        return context -> {
            JsonObject requestBodyAsJson = context.getBodyAsJson();

            if (requestBodyAsJson.getJsonArray("servers") == null || requestBodyAsJson.getJsonArray("servers").isEmpty()) {
                context.response().end(new JsonObject().put("error", JsonErrors.EMPTY_JSON_PARAMETERS).encodePrettily());
                return;
            }

            List<UUID> serversUUIDs = new ArrayList<>();
            try {
                requestBodyAsJson.getJsonArray("servers").stream()
                        .map(o -> (JsonObject) o)
                        .forEach(jo -> serversUUIDs.add(UUID.fromString(jo.getString("server_uuid"))));
            } catch (Exception e) {
                context.response().end(new JsonObject().put("error", DataErrors.INVALID_UUID).encodePrettily());
                return;
            }

            serverNioDao.delete(serversUUIDs, ar1 -> {
                if (ar1.succeeded()) {
                    context.response().setStatusCode(200).end();
                } else {
                    context.response().setStatusCode(404).end();
                }
            });
        };
    }
}
