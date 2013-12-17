package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Score extends BaseModel<Score> implements Comparable<Score> {

    @ManyToOne
    public Game game;

    @ManyToOne
    public Player player;

    @ManyToOne
    public Stage stage;

    @ManyToOne
    public Mode mode;

    @ManyToOne
    public Difficulty difficulty;

    @ManyToOne
    public Platform platform;

    public Long value;

    @Lob
    public String comment;

    public String photo;

    public String replay;

    public Score(Game game, Player player, Stage stage, Mode mode, Difficulty difficulty, String comment, Platform platform, Long value, String photo, String replay) {
        this.game = game;
        this.player = player;
        this.stage = stage;
        this.mode = mode;
        this.difficulty = difficulty;
        this.comment = comment;
        this.platform = platform;
        this.value = value;
        this.photo = photo;
        this.replay = replay;
    }

    public String formattedDate() {
        return getUpdatedSince();
    }

    public String formattedValue() {
        String score = String.valueOf(value);
        StringBuilder sb = new StringBuilder();
        int chara = 0;
        for (int i = score.length() - 1; i >= 0; i--) {
            if (chara % 3 == 0 && chara != 0) {
                sb.append(".");
            }
            sb.append(score.charAt(i));
            chara++;
        }
        score = sb.reverse().toString();
        return score;
    }

    public String formattedRank() {
        int value = rank();
        int hundredRemainder = value % 100;
        int tenRemainder = value % 10;
        if (hundredRemainder - tenRemainder == 10) {
            return value + "th";
        }
        switch (tenRemainder) {
            case 1:
                return value + "st";
            case 2:
                return value + "nd";
            case 3:
                return value + "rd";
            default:
                return value + "th";
        }
    }

    public int rank() {
        List<Score> scores = new ArrayList<Score>(game.scores(difficulty, mode));
        scores = Scores.keepBestScoresForEachPlayer(scores);
        for (int i = 0; i < scores.size(); i++) {
            if (this.equals(scores.get(i))) {
                return i + 1;
            }
        }
        return 0;
    }

    public boolean concerns(Difficulty difficulty) {
        return difficulty.equals(this.difficulty);
    }

    public boolean concerns(Mode mode) {
        return mode.equals(this.mode);
    }

    @Override
    public String toString() {
        return formattedValue();
    }

    @Override
    public int compareTo(Score score) {
        int i = score.value.compareTo(this.value);
        if (i == 0) {
            return score.stage.id.compareTo(this.stage.id);
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        return value.equals(((Score) o).value);
    }

    public boolean isWorstThanOlders() {
        for (Score score : player.bestScores()) {
            if (score.isWorstThan(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWorstThan(Score score) {
        if(isComparableWith(score)) {
            return score.compareTo(this) > 0;
        }
        return false;
    }

    private boolean isComparableWith(Score score) {
        return hasDifficulty(score.difficulty) && hasMode(score.mode) && hasGame(score.game);
    }

    private boolean hasDifficulty(Difficulty difficulty) {
        if(difficulty == null && this.difficulty == null) {
            return true;
        }
        if(difficulty == null) {
            return false;
        }
        if(this.difficulty == null) {
            return false;
        }
        return this.difficulty.equals(difficulty);
    }

    private boolean hasGame(Game game) {
        return this.game.equals(game);
    }

    private boolean hasMode(Mode mode) {
        if(mode == null && this.mode == null) {
            return true;
        }
        if(mode == null) {
            return false;
        }
        if(this.mode == null) {
            return false;
        }
        return this.mode.equals(mode);
    }

    public boolean isPlayedBy(Player player) {
        if(player == null) {
            return false;
        }
        return this.player.equals(player);
    }

}
