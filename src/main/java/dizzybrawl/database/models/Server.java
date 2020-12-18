package dizzybrawl.database.models;

import dizzybrawl.database.models.format.JsonTransformable;
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

    public static final String SERVER_UUID = "server_uuid";
    public static final String IP_V4 = "ip_v4";
    public static final String PLAYERS_IN_SERVER_COUNT = "players_in_server_count";

    @Id
    @Column(name = SERVER_UUID,
            unique = true,
            nullable = false)
    private UUID serverUUID;

    @Column(name = IP_V4,
            unique = true,
            nullable = false)
    private String ipV4;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = GameMode.GAME_MODE_ID,
                nullable = false)
    private GameMode gameMode;

    @Column(name = PLAYERS_IN_SERVER_COUNT,
            nullable = false)
    private int playersInGameServerCount;

    public Server() {
        this.gameMode = GameMode.createEmpty();
    }

    public Server(JsonObject jsonServer) {
        this();

        this.serverUUID = JsonUtils.getElse(jsonServer, null, UUID.class).apply(SERVER_UUID);
        this.ipV4 = JsonUtils.getElse(jsonServer, null, String.class).apply(IP_V4);
        this.gameMode.setGameModeId(JsonUtils.getElse(jsonServer, 0).apply(GameMode.GAME_MODE_ID));
        this.playersInGameServerCount = JsonUtils.getElse(jsonServer, 0).apply(PLAYERS_IN_SERVER_COUNT);
    }

    public Server(Row sqlRowServer) {
        this();

        this.serverUUID = SqlRowUtils.getElse(sqlRowServer, null, UUID.class).apply(SERVER_UUID);
        this.ipV4 = SqlRowUtils.getElse(sqlRowServer, null, String.class).apply(IP_V4);
        this.gameMode.setGameModeId(SqlRowUtils.getElse(sqlRowServer, 0).apply(GameMode.GAME_MODE_ID));
        this.gameMode.setName(SqlRowUtils.getElse(sqlRowServer, null, String.class).apply(GameMode.GAME_MODE_NAME));
        this.playersInGameServerCount = SqlRowUtils.getElse(sqlRowServer, 0).apply(PLAYERS_IN_SERVER_COUNT);
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

    public int getPlayersInGameServerCount() {
        return playersInGameServerCount;
    }

    public void setPlayersInGameServerCount(int playersInGameServerCount) {
        this.playersInGameServerCount = playersInGameServerCount;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put(SERVER_UUID, serverUUID == null ? null : serverUUID.toString())
                .put(IP_V4, ipV4)
                .put(GameMode.GAME_MODE_ID, (gameMode != null && gameMode.getGameModeId() != 0) ? gameMode.getGameModeId() : 0)
                .put(GameMode.GAME_MODE_NAME, (gameMode != null && gameMode.getName() != null) ? gameMode.getName() : null)
                .put(PLAYERS_IN_SERVER_COUNT, playersInGameServerCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return playersInGameServerCount == server.playersInGameServerCount && Objects.equals(serverUUID, server.serverUUID) && Objects.equals(ipV4, server.ipV4) && Objects.equals(gameMode, server.gameMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverUUID, ipV4, gameMode, playersInGameServerCount);
    }
}
