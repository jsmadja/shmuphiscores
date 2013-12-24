package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;

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
            int oneCreditCount = 0;
            int firstRankCount = 0;
            int top3Count = 0;
            int top10Count = 0;
            for (Score score : player.bestScores()) {
                int rank = score.rank();
                if (score.isOneCredited()) {
                    oneCreditCount++;
                }
                if (rank == 1) {
                    firstRankCount++;
                }
                if (rank <= 3) {
                    top3Count++;
                }
                if (rank <= 10) {
                    top10Count++;
                }
            }
            counts.put(player, new Counts(firstRankCount, top3Count, top10Count, oneCreditCount));
        }

        return ok(views.html.players.render(players, counts));
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
