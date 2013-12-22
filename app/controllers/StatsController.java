package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import models.Game;
import models.Platform;
import models.Player;
import models.Score;
import org.joda.time.DateMidnight;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import javax.annotation.Nullable;
import java.util.*;

import static com.avaje.ebean.Expr.le;
import static com.google.common.collect.Collections2.transform;
import static models.Score.getBestScoreFor;

public class StatsController extends Controller {

    public static Result index() {
        return ok(stats.render(Game.finder.all(), Platform.finder.all(), Score.finder.all()));
    }

    public static String scoresPerDay() {
        List<String> scores = new ArrayList<String>();

        DateMidnight dt = new DateMidnight(2013, 11, 29);
        while (dt.isBeforeNow()) {
            scores.add(Ebean.createQuery(Score.class).where(le("createdAt", dt.toDate())).findRowCount() + "");
            dt = dt.plusDays(1);
        }
        return Joiner.on(",").join(scores);
    }

    public static Collection<String> playedGameTitlesForPlayer(Player player) {
        List<Score> scores = getScoresOrderByProgression(player);
        return transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                return score.getGameTitle();
            }
        });
    }

    private static List<Score> getScoresOrderByProgression(Player player) {
        List<Score> scores = player.bestScores();
        scores = new ArrayList<Score>(Collections2.filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return score.rank() > 1;
            }
        }));
        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score score, Score score2) {
                return getBestScoreFor(score2.game, score2.mode, score2.difficulty).compareTo(getBestScoreFor(score.game, score.mode, score.difficulty));
            }
        });
        return scores;
    }

    public static String bestScoresByGameForPlayer(Player player) {
        List<Score> scores = getScoresOrderByProgression(player);
        return Joiner.on(",").join(transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                return getBestScoreFor(score.game, score.mode, score.difficulty).value.toString();
            }
        }));
    }

    public static String averageScoresByGameForPlayer(Player player) {
        List<Score> scores = getScoresOrderByProgression(player);
        return Joiner.on(",").join(transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                return score.game.averageScoreAsLong(score.difficulty, score.mode).toString();
            }
        }));
    }

    public static String bestPlayerScoresByGameForPlayer(Player player) {
        List<Score> scores = getScoresOrderByProgression(player);
        return Joiner.on(",").join(transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                return score.value.toString();
            }
        }));
    }


}
