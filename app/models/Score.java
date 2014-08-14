package models;

import com.avaje.ebean.Ebean;
import formatters.ScoreFormatter;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import java.util.Date;

import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
import static java.text.MessageFormat.format;

@Entity
public class Score extends BaseModel<Score> implements Comparable<Score> {


    @XmlTransient
    @ManyToOne
    public Game game;

    @XmlTransient
    @ManyToOne
    public Player player;

    @XmlTransient
    @ManyToOne
    public Stage stage;

    @XmlTransient
    @ManyToOne
    public Mode mode;

    @XmlTransient
    @ManyToOne
    public Difficulty difficulty;

    @ManyToOne
    @XmlTransient
    public Platform platform;

    @Lob
    @XmlAttribute
    public String comment;

    @XmlAttribute
    public String photo;

    @XmlAttribute
    public String replay;

    @Lob
    @Column(name = "photo_base_64")
    public String photoBase64;

    public static Finder<Long, Score> finder = new Model.Finder(Long.class, Score.class);

    @Transient
    @XmlAttribute(name = "stage")
    public String stageName;

    @XmlAttribute
    public Long value;

    @Transient
    @XmlAttribute(name = "player")
    public String playerName;

    @XmlAttribute
    private Integer rank;

    @Transient
    public Long gapWithPreviousScore;

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

    public Score(Long id, Game game, Player player, Stage stage, Mode mode, Difficulty difficulty, String comment, Platform platform, Long value, String photo, String replay, Integer rank) {
        this(game, player, stage, mode, difficulty, comment, platform, value, photo, replay);
        this.rank = rank;
        this.id = id;
    }

    public Score(Game game, Player player, Stage stage, Mode mode, Difficulty difficulty, String comment, Platform platform, Long value, String photoBase64) {
        this.game = game;
        this.player = player;
        this.stage = stage;
        this.mode = mode;
        this.difficulty = difficulty;
        this.comment = comment;
        this.platform = platform;
        this.value = value;
        this.photoBase64 = photoBase64;
    }

    public String formattedDate() {
        return getUpdatedSince();
    }

    public String formattedValue() {
        return ScoreFormatter.format(value);
    }

    public String formattedRank() {
        Integer value = rank();
        if (value == null) {
            value = 0;
        }
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

    public Integer rank() {
        return rank;
    }

    @Override
    public String toString() {
        return formattedValue();
    }

    @Override
    public int compareTo(Score score) {
        int i = score.value.compareTo(this.value);
        if (i == 0) {
            if (score.stage != null && this.stage != null) {
                return score.stage.id.compareTo(this.stage.id);
            }
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

    boolean isWorstThan(Score score) {
        if (isComparableWith(score)) {
            return score.compareTo(this) > 0;
        }
        return false;
    }

    private boolean isComparableWith(Score score) {
        return hasDifficulty(score.difficulty) && hasMode(score.mode) && hasGame(score.game);
    }

    private boolean hasDifficulty(Difficulty difficulty) {
        if (difficulty == null && this.difficulty == null) {
            return true;
        }
        if (difficulty == null) {
            return false;
        }
        if (this.difficulty == null) {
            return false;
        }
        return this.difficulty.equals(difficulty);
    }

    private boolean hasGame(Game game) {
        return this.game.equals(game);
    }

    private boolean hasMode(Mode mode) {
        if (mode == null && this.mode == null) {
            return true;
        }
        if (mode == null) {
            return false;
        }
        if (this.mode == null) {
            return false;
        }
        return this.mode.equals(mode);
    }

    public boolean isPlayedBy(Player player) {
        if (player == null) {
            return false;
        }
        return this.player.equals(player);
    }

    public String getGameTitle() {
        String title = game.title;
        if (mode != null) {
            title += " " + mode.name;
        }
        if (difficulty != null) {
            title += " " + difficulty.name;
        }
        return title;
    }

    public static Score getBestScoreFor(Game game, Mode mode, Difficulty difficulty) {
        return Ebean.createQuery(Score.class).setMaxRows(1).orderBy("value desc").where(and(eq("game", game), and(eq("mode", mode), eq("difficulty", difficulty)))).findUnique();
    }

    public Double getGapWithTop() {
        Long max = getBestScoreFor(game, mode, difficulty).value;
        return (max - value) / (double) max;
    }

    public void updateRank(Integer rank) {
        this.rank = rank;
    }

    public boolean hasRank() {
        return rank != null;
    }

    public String tweet() {
        return format("{3} - {1} pts - {4} - {2}{5} - {0} {6}", formattedRank(), formattedValue(), player.name, game.title, (modeName() + " " + difficultyName()).trim(), twitterAccount(), "hiscores.shmup.com/game/" + game.id + "/" + game.getEscapedTitle());
    }

    private String twitterAccount() {
        if (player.twitter != null) {
            return " (" + player.twitter + ")";
        }
        return "";
    }

    private String difficultyName() {
        return difficulty == null ? "" : difficulty.name;
    }

    private String modeName() {
        return mode == null ? "" : mode.name;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isVip() {
        return player.isVip();
    }
}
