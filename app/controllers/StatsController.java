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

import static com.avaje.ebean.Expr.le;
import static java.util.stream.Collectors.toList;

public class StatsController extends Controller {

    public static Result index() {
        return ok(stats.render(Ebean.find(Game.class).findList(), Ebean.find(Platform.class).findList(), Ebean.find(Score.class).findList(), com.avaje.ebean.Ebean.find(Player.class).fetch("scores").findList().stream().filter(Player::isUnbeatable).sorted((p1, p2) -> p2.scores.size() - p1.scores.size()).collect(toList())));
    }

    public static String scoresPerDay() {
        List<String> scores = new ArrayList<String>();
        DateMidnight dt = new DateMidnight(2013, 11, 29);
        while (dt.isBeforeNow()) {
            int createdAt = Ebean.
                    createQuery(Score.class).
                    where(le("createdAt", dt.plusDays(1).toDate())).
                    findRowCount();
            scores.add(((double) createdAt) + "");
            dt = dt.plusMonths(1);
        }
        return Joiner.on(",").join(scores);
    }

}
