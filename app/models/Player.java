package models;

import com.avaje.ebean.annotation.Where;
import com.google.common.base.Predicate;
import controllers.PlayersController;
import org.apache.commons.lang3.StringUtils;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
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
import static com.avaje.ebean.Expr.ilike;
import static com.avaje.ebean.Expr.isNotNull;
import static com.avaje.ebean.Expr.or;
import static com.avaje.ebean.Expr.startsWith;
import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;

@Entity
public class Player extends BaseModel<Player> implements Comparable<Player> {

    public static Player guest = new Player(0L, "guest");
    public static Finder<Long, Player> finder = new Model.Finder(Long.class, Player.class);
    @XmlAttribute
    public String name;
    @XmlTransient
    public Long shmupUserId;
    @XmlTransient
    @OneToMany(mappedBy = "player")
    @Where(clause = "rank > 0")
    public List<Score> scores = new ArrayList<Score>();
    @XmlTransient
    @OneToMany(mappedBy = "player")
    public List<Score> allScores = new ArrayList<Score>();
    @XmlTransient
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

    @Override
    public String toString() {
        return name;
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

    public Collection<Score> bestReplayableScores() {
        return filter(bestScores(), new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return StringUtils.isNotBlank(score.replay);
            }
        });
    }

    public boolean hasReplays() {
        return !bestReplayableScores().isEmpty();
    }

    public boolean isVip() {
        return vip;
    }

    public boolean canImportScores() {
        return id == 1 // anzymus
                || id == 6 // yace
                || id == 42 // mickey
                || id == 137 // trizeal
                || id == 705 // Vzurkr
                || id == 191 // lerebours
                || id == 7 // SL
                || id == 231 // MKNIGHT
                || id == 269 // Yami
                || id == 30 // shadow gallery
                || id == 116 // Doudinou
                || id == 150 // Undef
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
                return o2.getUpdatedAt().compareTo(o1.getUpdatedAt());
            }
        });
        return scores.get(0);
    }

    public int computeOneCredit() {
        List<Score> oneCreditScores = find(Score.class).where(and(and(isNotNull("rank"), eq("player", this)), or(ilike("stage.name", "%all%"), startsWith("stage.name", "2-")))).findList();
        Set<String> uniqueOneCreditScores = new HashSet<String>();
        for (Score oneCreditScore : oneCreditScores) {
            String gameTitle = oneCreditScore.getGameTitle();
            String mode = oneCreditScore.modeName();
            String difficulty = oneCreditScore.difficultyName();
            uniqueOneCreditScores.add(gameTitle + mode + difficulty);
        }
        return uniqueOneCreditScores.size();
    }

    public PlayersController.Counts getCounts() {
        return counts;
    }

    public void setCounts(PlayersController.Counts counts) {
        this.counts = counts;
    }

    @Override
    public int compareTo(Player p) {
        return this.name.compareToIgnoreCase(p.name);
    }
}
