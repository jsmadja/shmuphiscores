package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;
import models.*;
import org.joda.time.DateMidnight;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.avaje.ebean.Expr.le;
import static com.google.common.collect.Collections2.filter;

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

    public static String scoreCategories(Game game, Difficulty difficulty, Mode mode) {
        return Joiner.on(",").join(getCategories(game, difficulty, mode));
    }

    private static List<Long> getCategories(Game game, Difficulty difficulty, Mode mode) {
        List<Long> scoreCategories = new ArrayList<Long>();
        Collection<Score> scores = game.bestScoresByPlayers(difficulty, mode);
        Ordering<Score> ordering = new Ordering<Score>() {
            @Override
            public int compare(Score left, Score right) {
                return Longs.compare(left.value, right.value);
            }
        };
        Long min = ordering.min(scores).value;
        Long max = ordering.max(scores).value;
        if(min.equals(max)) {
            min = 0L;
        }
        long step = (max - min) / STEP;
        for (int i = 1; i <= STEP; i++) {
            scoreCategories.add(i * step);
        }
        scoreCategories.add(max);
        return scoreCategories;
    }

    public static String playerPerCategories(Game game, Difficulty difficulty, Mode mode) {
        final List<Long> scoreCategories = getCategories(game, difficulty, mode);
        List<Integer> playerPerCategories = new ArrayList<Integer>();
        Collection<Score> scores = game.bestScoresByPlayers(difficulty, mode);
        for (int i = 0; i <= STEP; i++) {
            final long min = i * scoreCategories.get(0);
            final long max = (i + 1) * scoreCategories.get(0);
            int count = filter(scores, new Predicate<Score>() {
                @Override
                public boolean apply(@Nullable Score score) {
                    return min <= score.value && score.value < max;
                }
            }).size();
            playerPerCategories.add(count);
        }
        return Joiner.on(",").join(playerPerCategories);
    }

    static int STEP = 4;
}
