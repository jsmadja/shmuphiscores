package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.game;

public class Game extends Controller {

    public static Result index() {
        return ok(game.render());
    }

}
