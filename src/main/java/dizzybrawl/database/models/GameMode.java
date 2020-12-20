package dizzybrawl.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "game_mode")
public class GameMode {

    public static final String GAME_MODE_ID = "game_mode_id";
    public static final String GAME_MODE_NAME = "game_mode_name";

    @Id
    @Column(name = GAME_MODE_ID,
            unique = true,
            nullable = false)
    private int gameModeId;

    @Column(name = GAME_MODE_NAME,
            nullable = false)
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMode gameMode = (GameMode) o;
        return gameModeId == gameMode.gameModeId &&
                name == gameMode.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameModeId, name);
    }
}
