package models;

import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import formatters.ScoreFormatter;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static models.Scores.keepBestScoresForEachPlayer;

@Entity
public class Game extends BaseModel<Game> {

    public String cover;

    public String title;

    public String thread;

    @OneToMany(mappedBy = "game")
    public List<Score> scores;

    @OrderBy("name")
    @OneToMany(mappedBy = "game")
    public List<Platform> platforms;

    @OrderBy("name")
    @OneToMany(mappedBy = "game")
    public List<Difficulty> difficulties;

    @OrderBy("name")
    @OneToMany(mappedBy = "game")
    public List<Mode> modes;

    @OneToMany(mappedBy = "game")
    public List<Stage> stages;

    public static Finder<Long, Game> finder = new Model.Finder(Long.class, Game.class);

    public Game(String title, String cover, String thread) {
        this.title = title;
        this.cover = cover;
        this.thread = thread;
    }

    public Collection<Score> scores(final Difficulty difficulty, final Mode mode) {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        return filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return (difficulty == null || score.concerns(difficulty)) && (mode == null || score.concerns(mode));
            }
        });
    }

    public Collection<Score> bestScoresByPlayers(final Difficulty difficulty, final Mode mode) {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        if(difficulty == null) {
            return keepBestScoresForEachPlayer(filterBy(mode));
        }
        return keepBestScoresForEachPlayer(filterBy(difficulty, mode));
    }

    public String averageScore(Difficulty difficulty, Mode mode) {
        BigDecimal sum = new BigDecimal(0);
        Collection<Score> scores = bestScoresByPlayers(difficulty, mode);
        for (Score score : scores) {
            sum = sum.add(new BigDecimal(score.value));
        }
        Long average = sum.divideToIntegralValue(new BigDecimal(scores.size())).longValue();
        return ScoreFormatter.format(average);
    }

    public Collection<Score> bestScores() {
        List<Score> bestScores = new ArrayList<Score>();
        if (scores == null) {
            return bestScores;
        }

        if (difficulties.isEmpty()) {
            if (modes.isEmpty()) {
                return keepBestScoresForEachPlayer(scores);
            }
            for (Mode mode : modes) {
                bestScores.addAll(keepBestScoresForEachPlayer(filterBy(mode)));
            }
            return bestScores;
        }

        for (final Difficulty difficulty : difficulties) {
            if (modes.isEmpty()) {
                bestScores.addAll(keepBestScoresForEachPlayer(filterBy(difficulty)));
            } else {
                for (final Mode mode : modes) {
                    bestScores.addAll(keepBestScoresForEachPlayer(filterBy(difficulty, mode)));
                }
            }
        }
        return bestScores;
    }

    private List<Score> filterBy(final Difficulty difficulty, final Mode mode) {
        return new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return (difficulty == null || score.concerns(difficulty)) && (mode == null || score.concerns(mode));
            }
        }));
    }

    private List<Score> filterBy(final Difficulty difficulty) {
        return new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return difficulty == null || score.concerns(difficulty);
            }
        }));
    }

    private List<Score> filterBy(final Mode mode) {
        return new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return mode == null || score.concerns(mode);
            }
        }));
    }

    public String post() {
        return thread.replace("viewtopic.php?", "posting.php?mode=reply&");
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
}
