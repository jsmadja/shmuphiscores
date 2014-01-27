package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Joiner;
import models.Game;
import models.Platform;
import models.Player;
import models.Score;
import org.joda.time.DateMidnight;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Expr.*;

public class StatsController extends Controller {

    public static Result index() {
        return ok(stats.render(Game.finder.all(), Platform.finder.all(), Score.finder.all()));
    }

    public static String scoresPerDay() {
        List<String> scores = new ArrayList<String>();
        DateMidnight dt = new DateMidnight(2013, 11, 29);
        while (dt.isBeforeNow()) {
            int createdAt = Ebean.createQuery(Score.class).where(le("createdAt", dt.plusDays(1).toDate())).findRowCount();
            scores.add(((double) createdAt) / 10 + "");
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

    public static String gamesPerDay() {
        List<String> games = new ArrayList<String>();
        DateMidnight dt = new DateMidnight(2013, 11, 29);
        while (dt.isBeforeNow()) {
            games.add(Ebean.createQuery(Game.class).where(le("updatedAt", dt.plusDays(1).toDate())).findRowCount() + "");
            dt = dt.plusDays(1);
        }
        return Joiner.on(",").join(games);
    }

}
