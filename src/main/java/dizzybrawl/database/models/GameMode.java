package dizzybrawl.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "game_mode")
public class GameMode {

    @Id
    @Column(name = "game_mode_id",
            unique = true,
            nullable = false)
    private int gameModeId;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "players_in_game_server_count",
            nullable = false)
    private int playersInGameServerCount;

    public GameMode() {}

    public static GameMode createEmpty() {
        return new GameMode();
    }

    public int getGameModeId() {
        return gameModeId;
    }

    public void setGameModeId(int gameModeId) {
        this.gameModeId = gameModeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String gameModeType) {
        this.name = gameModeType;
    }

    public int getPlayersInGameServerCount() {
        return playersInGameServerCount;
    }

    public void setPlayersInGameServerCount(int playersInGameServerCount) {
        this.playersInGameServerCount = playersInGameServerCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMode gameMode = (GameMode) o;
        return gameModeId == gameMode.gameModeId &&
                playersInGameServerCount == gameMode.playersInGameServerCount &&
                name == gameMode.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameModeId, name, playersInGameServerCount);
    }
}
