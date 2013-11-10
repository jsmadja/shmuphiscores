package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.players;

public class Players extends Controller {

    public static Result index() {
        return ok(players.render());
    }

}
