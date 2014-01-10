package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import models.*;
import org.joda.time.DateMidnight;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import javax.annotation.Nullable;
import java.util.*;

import static com.avaje.ebean.Expr.le;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static java.util.Collections.sort;

public class StatsController extends Controller {

    public static final int MAX_GAMES_IN_GRAPH = 10;
    public static final Predicate<Score> NO_FIRST_PLACE = new Predicate<Score>() {
        @Override
        public boolean apply(@Nullable Score score) {
            return score.rank() > 1;
        }
    };

    public static Result index() {
        return ok(stats.render(Game.finder.all(), Platform.finder.all(), Score.finder.all()));
    }

    public static String scoresPerDay() {
        List<String> scores = new ArrayList<String>();
        DateMidnight dt = new DateMidnight(2013, 11, 29);
        while (dt.isBeforeNow()) {
            scores.add(Ebean.createQuery(Score.class).where(le("createdAt", dt.plusDays(1).toDate())).findRowCount() + "");
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
        scores = new ArrayList<Score>(filter(scores, NO_FIRST_PLACE));
        sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score score, Score score2) {
                return score2.getGapWithTop().compareTo(score.getGapWithTop());
            }
        });
        scores = scores.subList(0, scores.size() > MAX_GAMES_IN_GRAPH ? MAX_GAMES_IN_GRAPH : scores.size());
        return scores;
    }

    public static String bestScoresByGameForPlayer(Player player) {
        List<Score> scores = getScoresOrderByProgression(player);
        return Joiner.on(",").join(transform(scores, new Function<Score, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Score score) {
                return Score.getBestScoreFor(score.game, score.mode, score.difficulty).value.toString();
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

    public static String scoreCategories(Ranking ranking) {
        return Joiner.on(",").join(getCategories(ranking));
    }

    private static Collection<Long> getCategories(Ranking ranking) {
        TreeMap<Long, Score> scoresMaps = scoreMap(ranking);
        List<Long> scoreCategories = new ArrayList<Long>();
        Long min = scoresMaps.firstKey();
        Long max = scoresMaps.lastKey();
        if (min.equals(max)) {
            min = 0L;
        }
        long step = (max - min) / scoresMaps.size();
        scoreCategories.add(min);
        for (int i = 1; i < (scoresMaps.size() - 1); i++) {
            scoreCategories.add(scoreCategories.get(i - 1).longValue() + step);
        }
        scoreCategories.add(max);
        return scoreCategories;
    }

    public static String playerPerCategories(Ranking ranking) {
        TreeMap<Long, Score> scores = scoreMap(ranking);
        List<Integer> playerPerCategories = new ArrayList<Integer>();
        for (Long category : getCategories(ranking)) {
            playerPerCategories.add(scores.tailMap(category).size());
        }
        return Joiner.on(",").join(playerPerCategories);
    }

    private static TreeMap<Long, Score> scoreMap(Ranking ranking) {
        TreeMap<Long, Score> scoresMaps = new TreeMap<Long, Score>();
        for (Score score : ranking.getScores()) {
            scoresMaps.put(score.value, score);
        }
        return scoresMaps;
    }

}
