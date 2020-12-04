package dizzybrawl.database.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "game_mode")
public class GameMode {

    public enum GameModeType {
        BATTLE_ROYAL,
        RACE
    }

    @Id
    @Column(name = "game_mode_id",
            unique = true,
            nullable = false)
    private int gameModeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_mode_type",
            nullable = false)
    private GameModeType gameModeType;

    @Column(name = "players_in_game_server_count",
            nullable = false)
    private int playersInGameServerCount;

    public GameMode() {}

    public int getGameModeId() {
        return gameModeId;
    }

    public void setGameModeId(int gameModeId) {
        this.gameModeId = gameModeId;
    }

    public GameModeType getGameModeType() {
        return gameModeType;
    }

    public void setGameModeType(GameModeType gameModeType) {
        this.gameModeType = gameModeType;
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
                gameModeType == gameMode.gameModeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameModeId, gameModeType, playersInGameServerCount);
    }
}
