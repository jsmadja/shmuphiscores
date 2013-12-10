package controllers;

import models.Player;
import play.mvc.Controller;
import play.mvc.Result;

public class PlayerController extends Controller {

    public static Result index(Player player) {
        if (player == null) {
            return notFound();
        }
        return ok(views.html.player_read.render(player));
    }

}
