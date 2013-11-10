package controllers;

import models.Game;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.games;

public class Games extends Controller {

    public static Result index() {
        return ok(games.render(Game.finder.all()));
    }

}
