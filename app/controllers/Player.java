package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.player;

public class Player extends Controller {

    public static Result index() {
        return ok(player.render());
    }

}
