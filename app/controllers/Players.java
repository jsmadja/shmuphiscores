package controllers;

import com.avaje.ebean.Ebean;
import models.Player;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

public class Players extends Controller {

    public static Result index() {
        List<Player> players = Ebean.createQuery(Player.class).findList();
        sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player, Player player2) {
                return player.name.compareToIgnoreCase(player2.name);
            }
        });
        return ok(views.html.players.render(players));
    }

}
