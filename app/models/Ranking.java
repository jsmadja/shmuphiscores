package models;

import java.util.Collection;

public class Ranking {
    public Game game;
    public Difficulty difficulty;
    public Mode mode;

    public Ranking(Game game) {
        this.game = game;
    }

    public Ranking(Game game, Difficulty difficulty) {
        this(game);
        this.difficulty = difficulty;
    }

    public Ranking(Game game, Mode mode) {
        this(game);
        this.mode = mode;
    }

    public Ranking(Game game, Difficulty difficulty, Mode mode) {
        this(game);
        this.difficulty = difficulty;
        this.mode = mode;
    }

    public Collection<Score> getScores() {
        return game.bestScoresByPlayers(difficulty, mode);
    }
}
