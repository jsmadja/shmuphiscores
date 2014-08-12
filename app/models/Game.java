package models;

import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
import static com.google.common.collect.Collections2.filter;

@Entity
public class Game extends BaseModel<Game> implements Comparable<Game> {

    @XmlAttribute
    public String thread;

    @XmlAttribute
    public String cover;

    @XmlAttribute
    public String title;

    @XmlTransient
    @OneToMany(mappedBy = "game")
    public List<Score> scores;

    @XmlElementWrapper
    @XmlElement(name = "platform")
    @OrderBy("name")
    @OneToMany(mappedBy = "game")
    public List<Platform> platforms;

    @XmlElementWrapper
    @XmlElement(name = "difficulty")
    @OrderBy("sortOrder")
    @OneToMany(mappedBy = "game")
    public List<Difficulty> difficulties;

    @XmlElementWrapper
    @XmlElement(name = "mode")
    @OrderBy("sortOrder")
    @OneToMany(mappedBy = "game")
    public List<Mode> modes;

    @XmlElementWrapper
    @XmlElement(name = "stage")
    @OrderBy("sortOrder")
    @OneToMany(mappedBy = "game")
    public List<Stage> stages;

    public static Finder<Long, Game> finder = new Model.Finder(Long.class, Game.class);

    @XmlElementWrapper(name = "rankings")
    @XmlElement(name = "ranking")
    private List<Ranking> initializedRankings;

    @XmlTransient
    private boolean generalRanking;

    public Game(String title, String cover, String thread) {
        this.title = title;
        this.cover = cover;
        this.thread = thread;
    }

    public List<Ranking> rankings() {
        List<Ranking> rankings = new ArrayList<Ranking>();
        if (generalRanking) {
            rankings.add(createGeneralRanking());
        }
        if (modes.isEmpty()) {
            if (difficulties.isEmpty()) {
                rankings.add(createGeneralRanking());
            } else {
                for (Difficulty difficulty : difficulties) {
                    rankings.add(new Ranking(findBestScoresByVIPPlayers(difficulty, null), difficulty));
                }
            }
        } else {
            for (Mode mode : modes) {
                if (difficulties.isEmpty()) {
                    rankings.add(new Ranking(findBestScoresByVIPPlayers(null, mode), mode));
                } else {
                    for (Difficulty difficulty : difficulties) {
                        rankings.add(new Ranking(findBestScoresByVIPPlayers(difficulty, mode), difficulty, mode));
                    }
                }
            }
        }
        return rankings;
    }

    private Ranking createGeneralRanking() {
        Ranking ranking = new Ranking(findBestScoresByVIPPlayers());
        List<Score> scores = new ArrayList<Score>();
        for (int rank = 0; rank < ranking.scores.size(); rank++) {
            Score score = ranking.scores.get(rank);
            scores.add(new Score(score.id, score.game, score.player, score.stage, score.mode, score.difficulty, score.comment, score.platform, score.value, score.photo, score.replay, rank + 1));
        }
        Ranking ranking1 = new Ranking(scores);
        ranking1.general = true;
        return ranking1;
    }

    private Collection<Score> findBestScoresByVIPPlayers(final Difficulty difficulty, final Mode mode) {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        List<Score> scores = Score.finder.where(and(eq("game", this), and(eq("difficulty", difficulty), eq("mode", mode)))).orderBy("value desc").findList();
        return keepBestScoreByVIPPlayer(scores);
    }

    private Collection<Score> findBestScoresByVIPPlayers() {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        List<Score> scores = Score.finder.where(eq("game", this)).orderBy("value desc").findList();
        return keepBestScoreByVIPPlayer(scores);
    }

    private Collection<Score> keepBestScoreByVIPPlayer(List<Score> scores) {
        final Set<Player> players = new HashSet<Player>();
        Collection<Score> filtered = filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                if (players.contains(score.player)) {
                    return false;
                }
                if (!score.player.isVip()) {
                    return false;
                }
                players.add(score.player);
                return true;
            }
        });
        ArrayList<Score> scores1 = new ArrayList<Score>(filtered);
        return scores1;
    }

    public String post() {
        return thread.replace("viewtopic.php?", "posting.php?mode=reply&f=20&");
    }

    @Override
    public String toString() {
        return title;
    }

    public static List<Game> findAll() {
        return Ebean.find(Game.class).order("title").findList();
    }

    public String getEscapedTitle() {
        String s = title.replaceAll("[^a-zA-Z0-9]", "_");
        s = s.replaceAll("_(_)*", "_");
        if (s.endsWith("_")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public void resetRankings() {
        for (Score score : scores) {
            score.updateRank(null);
            score.update();
        }
        rankings();
    }

    public String getCoverType() {
        if (cover.endsWith("jpg") || cover.endsWith("jpeg")) {
            return "image/jpeg";
        }
        if (cover.endsWith("png")) {
            return "image/png";
        }
        return "image/gif";
    }

    @Override
    public int compareTo(Game game) {
        return this.title.compareTo(game.title);
    }
}
