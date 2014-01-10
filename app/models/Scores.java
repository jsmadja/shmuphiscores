package models;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;

public class Scores {

    public static List<Score> keepBestScoresForEachPlayer(List<Score> scores) {
        sort(scores);
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

}
