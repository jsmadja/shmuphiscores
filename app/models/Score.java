package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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

    public int value;

    public String comment;

    public String photo;

    public Score(Game game, Player player, Stage stage, Mode mode, Difficulty difficulty, String comment, Platform platform, int value, String photo) {
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
        return "5th";
    }

    public boolean concerns(Platform platform) {
        return platform.equals(this.platform);
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
        return Integer.valueOf(this.value).compareTo(Integer.valueOf(score.value));
    }
}
