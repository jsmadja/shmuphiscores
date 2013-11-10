package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.players;

public class Players extends Controller {

    public static Result index() {
        return ok(players.render(Ebean.createQuery(models.Player.class).orderBy("name").findList()));
    }

}
