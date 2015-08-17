package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static views.html.players.render;

public class PlayersController extends Controller {

    public static Result index() {
        List<Player> players = Ebean.createQuery(Player.class).fetch("scores").findList();
        players = new ArrayList<Player>(filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(@Nullable Player player) {
                return !player.scores.isEmpty();
            }
        }));
        for (Player player : players) {
            int oneCreditCount = 0;
            int firstRankCount = 0;
            int secondRankCount = 0;
            int thirdRankCount = 0;
            List<Score> scores = player.scores;
            for (Score score : scores) {
                if (score.onecc) {
                    oneCreditCount++;
                }
                Integer rank = score.rank;
                if (rank == 1) {
                    firstRankCount++;
                } else if (score.rank == 2) {
                    secondRankCount++;
                } else if (score.rank == 3) {
                    thirdRankCount++;
                }
            }
            Counts playerCounts = new Counts(firstRankCount, secondRankCount, thirdRankCount, oneCreditCount);
            player.setCounts(playerCounts);
        }
        return ok(render(players));
    }

    public static class Counts {
        public Integer firstRankCount;
        public Integer secondRankCount;
        public Integer thirdRankCount;
        public Integer oneCreditCount;

        public Counts(int firstRankCount, int secondRankCount, int thirdRankCount, int oneCreditCount) {
            this.firstRankCount = firstRankCount;
            this.secondRankCount = secondRankCount;
            this.thirdRankCount = thirdRankCount;
            this.oneCreditCount = oneCreditCount;
        }
    }

}
