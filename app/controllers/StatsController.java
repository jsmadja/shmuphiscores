package controllers;

import models.Game;
import models.Platform;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

public class StatsController extends Controller {

    public static Result index() {
        return ok(stats.render(Game.finder.all(), Platform.finder.all(), Score.finder.all()));
    }

}
