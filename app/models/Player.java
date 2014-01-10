package models;

import com.google.common.base.Predicate;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;

@Entity
public class Player extends BaseModel<Player> {

    public static Player guest = new Player(0L, "guest");

    @XmlAttribute
    public String name;

    @XmlTransient
    public Long shmupUserId;

    @XmlTransient
    @OneToMany(mappedBy = "player")
    public List<Score> scores = new ArrayList<Score>();

    public static Finder<Long, Player> finder = new Model.Finder(Long.class, Player.class);

    public Player(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Player(String name) {
        this.name = name;
    }

    public List<Score> getScores() {
        return scores;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Player findOrCreatePlayer(String name) {
        Player player = Player.finder.where()
                .eq("name", name)
                .findUnique();
        if (player == null) {
            player = new Player(name);
            player.save();
        }
        return player;
    }


    public static Player findByShmupUserId(Long shmupUserId) {
        return Player.finder.where()
                .eq("shmupUserId", shmupUserId)
                .findUnique();
    }

    public List<Score> bestScores() {
        List<Score> bestScores = new ArrayList<Score>(scores);
        sort(bestScores);
        final Set<String> games = new HashSet<String>();
        bestScores = new ArrayList<Score>(filter(bestScores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                String key = score.game.title + "_" + score.difficulty + " " + score.mode;
                if (games.contains(key)) {
                    return false;
                }
                games.add(key);
                return true;
            }
        }));
        Collections.sort(bestScores, new Comparator<Score>() {
            @Override
            public int compare(Score score, Score score2) {
                return score.game.title.compareTo(score2.game.title);
            }
        });
        return bestScores;
    }

    public boolean isAuthenticated() {
        return !this.equals(guest);
    }

}
