package models;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import org.apache.commons.lang3.StringUtils;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
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

    @XmlTransient
    public String twitter;

    private boolean vip;

    public Player(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Player(String name) {
        this.name = name;
    }

    public Collection<String> playedGameTitlesForPlayer() {
        List<Score> scores = getScoresOrderByProgression();
        return transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                return score.getGameTitle();
            }
        });
    }

    public String bestPlayerScoresByGameForPlayer() {
        List<Score> scores = getScoresOrderByProgression();
        return Joiner.on(",").join(transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                return score.value.toString();
            }
        }));
    }

    public String bestScoresByGameForPlayer() {
        List<Score> scores = getScoresOrderByProgression();
        final List<Score> topPlayerScores = bestScores();
        return Joiner.on(",").join(transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                Long bestScore = Score.getBestScoreFor(score.game, score.mode, score.difficulty).value;
                for (Score score1 : topPlayerScores) {
                    boolean sameGame = score.game.equals(score1.game);
                    boolean sameMode = score1.mode == null ? true : score1.mode.equals(score.mode);
                    boolean sameDifficulty = score1.difficulty == null ? true : score1.difficulty.equals(score.difficulty);
                    if (sameGame && sameMode && sameDifficulty) {
                        bestScore -= score1.value;
                        break;
                    }
                }
                return bestScore.toString();
            }
        }));
    }

    private List<Score> getScoresOrderByProgression() {
        List<Score> scores = bestScores();
        sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score score, Score score2) {
                return score2.getGapWithTop().compareTo(score.getGapWithTop());
            }
        });
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
                || id == 42 // mickey
                || id == 137 // trizeal
                || id == 705 // Vzurkr
                || id == 191 // lerebours
                || id == 7 // SL
                || id == 231 // MKNIGHT
                || id == 269 // Yami
                ;
    }
}
