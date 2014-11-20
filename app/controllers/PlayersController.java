package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.google.common.base.Predicate;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.*;

import static com.avaje.ebean.Ebean.find;
import static com.avaje.ebean.Expr.*;
import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;
import static views.html.players.render;

public class PlayersController extends Controller {

    public static Result index() {
        List<Player> players = Ebean.createQuery(Player.class).findList();
        sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player, Player player2) {
                return player.name.compareToIgnoreCase(player2.name);
            }
        });
        players = new ArrayList<Player>(filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(@Nullable Player player) {
                return !player.scores.isEmpty();
            }
        }));
        Map<Player, Counts> counts = new HashMap<Player, Counts>();
        for (Player player : players) {
            int oneCreditCount = computeOneCredit(player);
            int firstRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 1))).findRowCount();
            int top3Count = find(Score.class).where(and(eq("player", player), le("rank", 3))).findRowCount();
            int top10Count = find(Score.class).where(and(eq("player", player), le("rank", 10))).findRowCount();
            counts.put(player, new Counts(firstRankCount, top3Count, top10Count, oneCreditCount));
        }
        return ok(render(players, counts));
    }

    private static int computeOneCredit(Player player) {
        List<Score> oneCreditScores = find(Score.class).where(and(eq("player", player), or(ilike("stage.name", "%all%"), Expr.startsWith("stage.name", "2-")))).findList();
        Set<String> uniqueOneCreditScores = new HashSet<String>();
        for (Score oneCreditScore : oneCreditScores) {
            String gameTitle = oneCreditScore.getGameTitle();
            String mode = oneCreditScore.modeName();
            String difficulty = oneCreditScore.difficultyName();
            uniqueOneCreditScores.add(gameTitle + mode + difficulty);
        }
        return uniqueOneCreditScores.size();
    }

    public static class Counts {
        public int firstRankCount;
        public int top3Count;
        public int top10Count;
        public int oneCreditCount;

        public Counts(int firstRankCount, int top3Count, int top10Count, int oneCreditCount) {
            this.firstRankCount = firstRankCount;
            this.top3Count = top3Count;
            this.top10Count = top10Count;
            this.oneCreditCount = oneCreditCount;
        }
    }

}
