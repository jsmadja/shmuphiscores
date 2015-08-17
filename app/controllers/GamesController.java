package controllers;

import com.avaje.ebean.Ebean;
import models.Game;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.games;

import java.util.List;

public class GamesController extends Controller {

    public static Result index() {
        List<Game> all = Ebean.find(Game.class).
                fetch("platforms").
                fetch("scores").
                findList();
        return ok(games.render(all));
    }

}
