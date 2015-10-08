package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Where;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import controllers.PlayersController;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.avaje.ebean.Ebean.find;
import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
import static com.avaje.ebean.Expr.gt;

@Entity
public class Player extends BaseModel<Player> implements Comparable<Player> {

    public static Player guest = new Player(0L, "guest");

    public static Model.Finder<Long, Player> finder = new Model.Finder(Long.class, Player.class);

    public String name;

    public Long shmupUserId;

    public boolean hideMedals;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    @Where(clause = "rank > 0")
    public List<Score> scores = new ArrayList<Score>();

    @OneToMany(mappedBy = "player")
    public List<Score> allScores = new ArrayList<Score>();

    public String twitter;

    private boolean vip;

    private PlayersController.Counts counts;

    public Player(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Player(String name) {
        this.name = name;
    }

    public static Player findOrCreatePlayer(String name) {
        Player player = Player.finder.where()
                .eq("name", name)
                .findUnique();
        if (player == null) {
            player = new Player(name);
            player.vip = true;
            player.save();
        }
        return player;
    }

    public static Player findByShmupUserId(Long shmupUserId) {
        return Player.finder.where()
                .eq("shmupUserId", shmupUserId)
                .findUnique();
    }

    public static List<Player> findAll() {
        return finder.orderBy("name").findList();
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isAuthenticated() {
        return !this.equals(guest);
    }

    public Collection<Score> bestReplayableScores() {
        return Score.finder.fetch("game").fetch("platform").fetch("mode").fetch("difficulty").where().not(eq("replay", "")).eq("player", this).findList();
    }

    public boolean hasReplays() {
        return !bestReplayableScores().isEmpty();
    }

    public boolean isVip() {
        return vip;
    }

    public boolean canImportScores() {
        return id == 1          // anzymus
                || id == 6      // yace
                || id == 42     // mickey
                || id == 137    // trizeal
                || id == 705    // Vzurkr
                || id == 191    // lerebours
                || id == 7      // SL
                || id == 231    // MKNIGHT
                || id == 269    // Yami
                || id == 30     // shadow gallery
                || id == 116    // Doudinou
                || id == 150    // Undef
                || id == 779    // Kat
                || id == 159    // Cormano
                || id == 226    // Radigo
                || id == 57     // Akaimakai
                || id == 223    // Guts
                ;
    }

    public boolean isAdministrator() {
        return id == 1;
    }

    public Score getLastScore() {
        List<Score> scores = new ArrayList<Score>(this.scores);
        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score o1, Score o2) {
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
            }
        });
        return scores.get(0);
    }

    public int computeOneCredit() {
        return oneccs().size();
    }

    public Collection<Score> oneccs() {
        List<Score> oneCreditScores = find(Score.class).fetch("platform").fetch("game").fetch("mode").fetch("difficulty").where().eq("player", this).eq("onecc", true).findList();
        List<Score> oneccs = new ArrayList<Score>();
        Set<String> uniqueOneCreditScores = new HashSet<String>();
        for (Score oneCreditScore : oneCreditScores) {
            String gameTitle = oneCreditScore.getGameTitle();
            String mode = oneCreditScore.modeName();
            String difficulty = oneCreditScore.difficultyName();
            String key = gameTitle + mode + difficulty;
            boolean add = uniqueOneCreditScores.add(key);
            if (add) {
                oneccs.add(oneCreditScore);
            }
        }
        return oneccs;
    }

    public PlayersController.Counts getCounts() {
        return counts;
    }

    public void setCounts(PlayersController.Counts counts) {
        this.counts = counts;
    }

    public PlayersController.Counts computeCounts() {
        Integer firstRankCount = find(Score.class).where(and(eq("player", this), eq("rank", 1))).findRowCount();
        int secondRankCount = find(Score.class).where(and(eq("player", this), eq("rank", 2))).findRowCount();
        int thirdRankCount = find(Score.class).where(and(eq("player", this), eq("rank", 3))).findRowCount();
        int oneCreditCount = computeOneCredit();
        return counts = new PlayersController.Counts(firstRankCount, secondRankCount, thirdRankCount, oneCreditCount);
    }

    @Override
    public int compareTo(Player p) {
        return this.name.compareToIgnoreCase(p.name);
    }

    public Versus getComparisonWith(Player p) {
        Versus versus = new Versus(this, p);
        List<Score> scores = this.scores;
        for (Score score : scores) {
            Score comparisonScore = p.getEquivalentScore(score);
            if (comparisonScore != null) {
                Versus.Comparison comparison = new Versus.Comparison(score.game, score.difficulty, score.mode, score, comparisonScore);
                versus.add(comparison);
            }
        }
        return versus;
    }

    private Score getEquivalentScore(Score reference) {
        for (Score score : scores) {
            if (score.game.equals(reference.game)) {
                if (score.hasMode(reference.mode) && score.hasDifficulty(reference.difficulty)) {
                    return score;
                }
            }
        }
        return null;
    }

    public Versus getBestVersus() {
        List<Player> all = Player.finder.where().join("scores").findList();
        all.remove(this);
        Versus bestVersus = null;
        for (Player opponent : all) {
            Versus versus = getComparisonWith(opponent);
            if (bestVersus == null || bestVersus.loseCount() < versus.loseCount()) {
                bestVersus = versus;
            }
        }
        return bestVersus;
    }

    public boolean isUnbeatable() {
        for (Score score : scores) {
            if (score.rank > 1) {
                return false;
            }
        }
        return !scores.isEmpty();
    }

    public List<Score> fetchScores() {
        return Score.finder.
                where(and(gt("rank", 0), eq("player", this))).
                fetch("platform").
                fetch("stage").
                fetch("game").
                fetch("mode").
                fetch("player").
                fetch("difficulty").
                findList();
    }

    public Score getBestScoreFor(Game game, Mode mode, Difficulty difficulty) {
        return Ebean.createQuery(Score.class).
                setMaxRows(1).
                orderBy(game.hasTimerScores() ? "value" : "value desc").
                where().eq("player", this).eq("game", game).eq("mode", mode).eq("difficulty", difficulty).
                findUnique();
    }

    public JsonNode json() {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.set("id", new LongNode(id));
        node.set("name", new TextNode(name));
        return node;
    }
}
