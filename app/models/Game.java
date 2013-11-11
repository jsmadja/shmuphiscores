package models;

import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.*;

import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;

@Entity
public class Game extends BaseModel<Game> {

    public String cover;

    public String title;

    public String thread;

    @OneToMany(mappedBy = "game")
    public List<Score> scores;

    @OneToMany(mappedBy = "game")
    public List<Platform> platforms;

    @OneToMany(mappedBy = "game")
    public List<Difficulty> difficulties;

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

    public Collection<Score> scores(final Platform platform, final Difficulty difficulty, final Mode mode) {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        return filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return (platform == null || score.concerns(platform)) && (difficulty == null || score.concerns(difficulty)) && (mode == null || score.concerns(mode));
            }
        });
    }

    public Collection<Score> scoresByPlayers(final Platform platform, final Difficulty difficulty, final Mode mode) {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        List<Score> filtered = new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return (platform == null || score.concerns(platform)) && (difficulty == null || score.concerns(difficulty)) && (mode == null || score.concerns(mode));
            }
        }));
        sort(filtered);
        filtered = Scores.keepBestScoresForEachPlayer(filtered);
        return filtered;
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
}
