package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Joiner;
import models.*;
import org.joda.time.DateMidnight;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import static com.avaje.ebean.Expr.le;

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

    private static Collection<Long> getCategories(Game game, Difficulty difficulty, Mode mode) {
        TreeMap<Long, Score> scoresMaps = scoreMap(game, difficulty, mode);
        List<Long> scoreCategories = new ArrayList<Long>();
        Long min = scoresMaps.firstKey();
        Long max = scoresMaps.lastKey();
        if (min.equals(max)) {
            min = 0L;
        }
        long step = (max - min) / scoresMaps.size();
        scoreCategories.add(min);
        for (int i = 1; i < (scoresMaps.size() - 1); i++) {
            scoreCategories.add(i * step);
        }
        scoreCategories.add(max);
        return scoreCategories;
    }

    public static String playerPerCategories(Game game, Difficulty difficulty, Mode mode) {
        TreeMap<Long, Score> scores = scoreMap(game, difficulty, mode);
        List<Integer> playerPerCategories = new ArrayList<Integer>();
        for (Long category : getCategories(game, difficulty, mode)) {
            playerPerCategories.add(scores.tailMap(category).size());
        }
        return Joiner.on(",").join(playerPerCategories);
    }

    private static TreeMap<Long, Score> scoreMap(Game game, Difficulty difficulty, Mode mode) {
        TreeMap<Long, Score> scoresMaps = new TreeMap<Long, Score>();
        for (Score score : game.bestScoresByPlayers(difficulty, mode)) {
            scoresMaps.put(score.value, score);
        }
        return scoresMaps;
    }

}
