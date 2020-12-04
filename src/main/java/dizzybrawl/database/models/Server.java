package dizzybrawl.database.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "server")
public class Server {

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

    public Server() {}

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
