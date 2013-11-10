package models;

import com.google.common.base.Predicate;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;

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

    @Override
    public String toString() {
        return title;
    }

}
