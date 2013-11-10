package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Game extends Controller {

    public static Result index(models.Game game) {
        return ok(views.html.game.render(game));
    }


}
