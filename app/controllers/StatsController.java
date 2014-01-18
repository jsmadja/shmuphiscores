package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import models.Game;
import models.Platform;
import models.Player;
import models.Score;
import org.joda.time.DateMidnight;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Expr.*;

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

    public static String playersPerDay() {
        List<String> players = new ArrayList<String>();
        DateMidnight dt = new DateMidnight(2013, 11, 29);
        while (dt.isBeforeNow()) {
            players.add(Ebean.createQuery(Player.class).where(and(isNotNull("shmupUserId"), le("updatedAt", dt.plusDays(1).toDate()))).findRowCount() + "");
            dt = dt.plusDays(1);
        }
        return Joiner.on(",").join(players);
    }

}
