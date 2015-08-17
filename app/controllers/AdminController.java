package controllers;

import actions.User;
import com.avaje.ebean.Ebean;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.admin;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class AdminController extends Controller {

    public static Result index() {
        List<Player> players = Ebean.createQuery(Player.class).findList();
        List<Player> toClean = players.stream().
                sorted((player, player2) -> player.name.compareToIgnoreCase(player2.name)).
                filter(player -> player.shmupUserId == null).collect(toList());
        return ok(admin.render(toClean, players));
    }

    public static Result save() {
        if (!User.current().isAuthenticated() && !User.current().isAdministrator()) {
            return unauthorized();
        }
        Http.Request request = request();
        Map<String, String[]> form = request.body().asFormUrlEncoded();
        Player from = Ebean.find(Player.class, Long.parseLong(form.get("from")[0]));
        Player to = Ebean.find(Player.class, Long.parseLong(form.get("to")[0]));
        for (Score score : from.scores) {
            score.player = to;
            score.update();
        }
        from.delete();
        return index();
    }

}
