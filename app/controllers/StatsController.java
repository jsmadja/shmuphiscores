package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Joiner;
import models.Game;
import models.Platform;
import models.Score;
import org.joda.time.DateMidnight;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Expr.le;

public class StatsController extends Controller {

    public static Result index() {
        return ok(stats.render(Game.finder.all(), Platform.finder.all(), Score.finder.all()));
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
