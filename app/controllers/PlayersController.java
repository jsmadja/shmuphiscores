package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.avaje.ebean.Ebean.find;
import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
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
            int oneCreditCount = player.computeOneCredit();
            int firstRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 1))).findRowCount();
            int secondRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 2))).findRowCount();
            int thirdRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 3))).findRowCount();
            counts.put(player, new Counts(firstRankCount, secondRankCount, thirdRankCount, oneCreditCount));
        }
        return ok(render(players, counts));
    }

    public static class Counts {
        public int firstRankCount;
        public int secondRankCount;
        public int thirdRankCount;
        public int oneCreditCount;

        public Counts(int firstRankCount, int secondRankCount, int thirdRankCount, int oneCreditCount) {
            this.firstRankCount = firstRankCount;
            this.secondRankCount = secondRankCount;
            this.thirdRankCount = thirdRankCount;
            this.oneCreditCount = oneCreditCount;
        }
    }

}
