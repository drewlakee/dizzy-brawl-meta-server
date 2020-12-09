package dizzybrawl.database.models;

import dizzybrawl.database.models.utils.JsonTransformable;
import dizzybrawl.database.utils.SqlRowUtils;
import dizzybrawl.http.utils.JsonUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "server")
public class Server implements JsonTransformable {

    @Id
    @Column(name = "server_uuid",
            unique = true,
            nullable = false)
    private UUID serverUUID;

    @Column(name = "ip_v4",
            unique = true,
            nullable = false)
    private String ipV4;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_mode_id",
                nullable = false)
    private GameMode gameMode;

    public Server() {
        this.gameMode = GameMode.createEmpty();
    }

    public Server(JsonObject jsonServer) {
        this();

        this.serverUUID = JsonUtils.getElse(jsonServer, null, UUID.class).apply("server_uuid");
        this.ipV4 = JsonUtils.getElse(jsonServer, null, String.class).apply("ip_v4");
        this.gameMode.setGameModeId(JsonUtils.getElse(jsonServer, 0).apply("game_mode_id"));
    }

    public Server(Row sqlRowServer) {
        this();

        this.serverUUID = SqlRowUtils.getElse(sqlRowServer, null, UUID.class).apply("server_uuid");
        this.ipV4 = SqlRowUtils.getElse(sqlRowServer, null, String.class).apply("ip_v4");
        this.gameMode.setGameModeId(SqlRowUtils.getElse(sqlRowServer, 0).apply("game_mode_id"));
        this.gameMode.setName(SqlRowUtils.getElse(sqlRowServer, null, String.class).apply("game_mode_name"));
    }

    public UUID getServerUUID() {
        return serverUUID;
    }

    public void setServerUUID(UUID serverUUID) {
        this.serverUUID = serverUUID;
    }

    public String getIpV4() {
        return ipV4;
    }

    public void setIpV4(String ipV4) {
        this.ipV4 = ipV4;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("server_uuid", serverUUID == null ? null : serverUUID.toString())
                .put("ip_v4", ipV4)
                .put("game_mode_id", (gameMode != null && gameMode.getGameModeId() != 0) ? gameMode.getGameModeId() : 0)
                .put("game_mode_name", (gameMode != null && gameMode.getName() != null) ? gameMode.getName() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(serverUUID, server.serverUUID) &&
                Objects.equals(ipV4, server.ipV4) &&
                Objects.equals(gameMode, server.gameMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverUUID, ipV4, gameMode);
    }
}
