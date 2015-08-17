package controllers;

import actions.User;
import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import models.Player;
import models.Score;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.admin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;

public class AdminController extends Controller {

    public static Result index() {
        List<Player> players = Ebean.createQuery(Player.class).findList();
        sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player, Player player2) {
                return player.name.compareToIgnoreCase(player2.name);
            }
        });
        List<Player> toClean = new ArrayList<Player>(filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(@Nullable Player player) {
                return player.shmupUserId == null;
            }
        }));
        return ok(admin.render(toClean, players));
    }

    public static Result save() {
        if (!User.current().isAuthenticated() && !User.current().isAdministrator()) {
            return unauthorized();
        }
        Http.Request request = request();
        Map<String, String[]> form = request.body().asFormUrlEncoded();
        Player from = Player.finder.byId(Long.parseLong(form.get("from")[0]));
        Player to = Player.finder.byId(Long.parseLong(form.get("to")[0]));
        for (Score score : from.scores) {
            score.player = to;
            score.update();
        }
        from.delete();
        return index();
    }

}
