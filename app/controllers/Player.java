package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Player extends Controller {

    public static Result index(models.Player player) {
        return ok(views.html.player.render(player));
    }

}
