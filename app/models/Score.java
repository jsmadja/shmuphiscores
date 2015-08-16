package models;

import com.avaje.ebean.Ebean;
import decorators.ScoreDecorator;
import formatters.ScoreFormatter;
import org.joda.time.DateTime;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.math.BigDecimal;

import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
import static java.math.RoundingMode.HALF_UP;
import static java.text.MessageFormat.format;

@Entity
public class Score extends BaseModel<Score> implements Comparable<Score> {

    public static Finder<Long, Score> finder = new Model.Finder(Long.class, Score.class);

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
    public Ship ship;

    @ManyToOne
    public Platform platform;

    @Lob
    public String comment;

    public String photo;

    public String replay;

    public BigDecimal value;

    public boolean onecc;

    public Integer progression;

    @Transient
    public Long gapWithPreviousScore;

    public Integer rank;

    public Score(Game game, Player player, Stage stage, Ship ship, Mode mode, Difficulty difficulty, String comment, Platform platform, BigDecimal value, String photo, String replay) {
        this.game = game;
        this.player = player;
        this.stage = stage;
        this.mode = mode;
        this.ship = ship;
        this.difficulty = difficulty;
        this.comment = comment;
        this.platform = platform;
        this.value = value;
        this.photo = photo;
        this.replay = replay;
        this.onecc = this.is1CC();
    }

    public Score(Long id, Game game, Player player, Stage stage, Ship ship, Mode mode, Difficulty difficulty, String comment, Platform platform, BigDecimal value, String photo, String replay, Integer rank) {
        this(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        this.rank = rank;
        this.id = id;
    }

    public Score(Game game, Player player, Stage stage, Ship ship, Mode mode, Difficulty difficulty, String comment, Platform platform, BigDecimal value) {
        this.game = game;
        this.player = player;
        this.stage = stage;
        this.ship = ship;
        this.mode = mode;
        this.difficulty = difficulty;
        this.comment = comment;
        this.platform = platform;
        this.value = value;
    }

    public Score(BigDecimal value) {
        this.value = value;
    }

    public static Score getBestScoreFor(Player player, Game game, Mode mode, Difficulty difficulty) {
        Score unique = Ebean.createQuery(Score.class).
                setMaxRows(1).
                orderBy(game.hasTimerScores() ? "value" : "value desc").
                where().eq("player", player).eq("game", game).eq("mode", mode).eq("difficulty", difficulty).
                findUnique();
        return unique;
    }

    public String formattedDate() {
        return getCreatedSince();
    }

    public String formattedDateInFrench() {
        return getCreatedSinceInFrench();
    }

    public String formattedValue() {
        if (mode != null && mode.isTimerScore()) {
            return ScoreFormatter.formatAsTime(value);
        }
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

    public String formattedRankInFrench() {
        if (rank() == null) {
            return "";
        }
        if (rank() == 1) {
            return "1ère";
        }
        return rank() + "ème";
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
        for (Score score : player.scores) {
            if (this.isWorstThan(score)) {
                return true;
            }
        }
        return false;
    }

    boolean isWorstThan(Score score) {
        if (isComparableWith(score)) {
            if (isNumericScore(score)) {
                return score.compareTo(this) < 0;
            }
            return score.compareTo(this) > 0;
        }
        return false;
    }

    private boolean isNumericScore(Score score) {
        return score.mode == null || !score.mode.isTimerScore();
    }

    private boolean isComparableWith(Score score) {
        return hasDifficulty(score.difficulty) && hasMode(score.mode) && hasGame(score.game);
    }

    public boolean hasDifficulty(Difficulty difficulty) {
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

    public boolean hasMode(Mode mode) {
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

    public void updateRank(Integer rank) {
        this.rank = rank;
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

    public String difficultyName() {
        return difficulty == null ? "" : difficulty.name;
    }

    public String modeName() {
        return mode == null ? "" : mode.name;
    }

    public String shipName() {
        return ship == null ? "" : ship.name;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isVip() {
        return player.isVip();
    }

    public boolean is1CC() {
        if (this.stage != null) {
            String stageName = this.stage.name.toLowerCase();
            return stageName.contains("all")
                    || stageName.startsWith("2-") || stageName.toLowerCase().startsWith("boss 2-")
                    || stageName.startsWith("3-") || stageName.toLowerCase().startsWith("boss 3-")
                    || stageName.startsWith("4-") || stageName.toLowerCase().startsWith("boss 4-")
                    || stageName.startsWith("5-") || stageName.toLowerCase().startsWith("boss 5-")
                    || stageName.startsWith("6-") || stageName.toLowerCase().startsWith("boss 6-")
                    || stageName.toUpperCase().contains("ENDLESS");
        }
        return false;
    }

    public ScoreDecorator decorator() {
        return new ScoreDecorator(this);
    }

    public Integer getOpponentCount() {
        return finder.where().eq("game", game).eq("difficulty", difficulty).eq("mode", mode).gt("rank", 0).findRowCount();
    }

    public Integer minutes() {
        if (isTimeScore()) {
            return new DateTime(this.value.longValue()).getMinuteOfHour();
        }
        return null;
    }

    public Integer seconds() {
        if (isTimeScore()) {
            return new DateTime(this.value.longValue()).getSecondOfMinute();
        }
        return null;
    }

    public Integer milliseconds() {
        if (isTimeScore()) {
            return new DateTime(this.value.longValue()).getMillisOfSecond();
        }
        return null;
    }

    public BigDecimal valuePoints() {
        if (isTimeScore()) {
            return null;
        }
        return this.value;
    }

    public boolean isTimeScore() {
        return this.mode != null && this.mode.isTimerScore();
    }

}
