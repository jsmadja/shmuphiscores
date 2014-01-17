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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static models.Scores.keepBestScoresForEachPlayer;

@Entity
public class Game extends BaseModel<Game> {

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

    public Game(String title, String cover, String thread) {
        this.title = title;
        this.cover = cover;
        this.thread = thread;
    }

    public List<Ranking> rankings() {
        List<Ranking> rankings = new ArrayList<Ranking>();
        if (modes.isEmpty()) {
            if (difficulties.isEmpty()) {
                rankings.add(new Ranking(findBestScoresByPlayers(null, null)));
            } else {
                for (Difficulty difficulty : difficulties) {
                    rankings.add(new Ranking(findBestScoresByPlayers(difficulty, null), difficulty));
                }
            }
        } else {
            for (Mode mode : modes) {
                if (difficulties.isEmpty()) {
                    rankings.add(new Ranking(findBestScoresByPlayers(null, mode), mode));
                } else {
                    for (Difficulty difficulty : difficulties) {
                        rankings.add(new Ranking(findBestScoresByPlayers(difficulty, mode), difficulty, mode));
                    }
                }
            }
        }
        return rankings;
    }

    private Collection<Score> findBestScoresByPlayers(final Difficulty difficulty, final Mode mode) {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        return keepBestScoresForEachPlayer(filterBy(difficulty, mode));
    }

    public int getScoreCount() {
        int count = 0;
        for (Ranking ranking : rankings()) {
            count += ranking.scores.size();
        }
        return count;
    }

    private List<Score> filterBy(final Difficulty difficulty, final Mode mode) {
        return new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return (difficulty == null || score.concerns(difficulty)) && (mode == null || score.concerns(mode));
            }
        }));
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
        String s = title.replaceAll("[^a-zA-Z]", "_");
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

    public void initializeRankings() {
        this.initializedRankings = rankings();
        for (Score score : scores) {
            score.playerName = score.player.name;
            score.stageName = score.stage == null ? null : score.stage.name;
        }
    }
}
