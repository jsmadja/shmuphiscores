package models;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Collections2.filter;

public class Scores {

    public static List<Score> keepBestScoresForEachPlayer(Collection<Score> scores) {
        final Set<Player> players = new HashSet<Player>();
        return new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                if(players.contains(score.player)) {
                    return false;
                }
                players.add(score.player);
                return true;
            }
        }));
    }

    public static List<Score> keepBestScoresForEachGame(Collection<Score> scores) {
        final Set<Game> games = new HashSet<Game>();
        return new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                if(games.contains(score.game)) {
                    return false;
                }
                games.add(score.game);
                return true;
            }
        }));
    }
}
