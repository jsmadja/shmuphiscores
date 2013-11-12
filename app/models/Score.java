package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

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

    public Score(Game game, Player player, Stage stage, Mode mode, Difficulty difficulty, String comment, Platform platform, Long value, String photo) {
        this.game = game;
        this.player = player;
        this.stage = stage;
        this.mode = mode;
        this.difficulty = difficulty;
        this.comment = comment;
        this.platform = platform;
        this.value = value;
        this.photo = photo;
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
        return score.value.compareTo(this.value);
    }

    @Override
    public boolean equals(Object o) {
        return value.equals(((Score) o).value);
    }
}
