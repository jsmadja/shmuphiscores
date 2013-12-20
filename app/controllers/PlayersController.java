package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import models.Player;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

public class PlayersController extends Controller {

    public static Result index() {
        List<Player> players = Ebean.createQuery(Player.class).findList();
        sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player, Player player2) {
                return player.name.compareToIgnoreCase(player2.name);
            }
        });
        players = new ArrayList<Player>(Collections2.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(@Nullable Player player) {
                return !player.scores.isEmpty();
            }
        }));
        return ok(views.html.players.render(players));
    }

}
