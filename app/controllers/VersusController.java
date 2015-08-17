package controllers;

import com.avaje.ebean.Ebean;
import models.Player;
import models.Versus;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.versus;

import java.util.Map;

public class VersusController extends Controller {

    public static Result index(Player player1, Player player2) {
        if(player1 == null) {
            return indexChooseVersus(player2);
        }
        return ok(versus.render(player1.getComparisonWith(player2), Player.findAll()));
    }

    public static Result indexChooseVersus(Player player1) {
        return ok(versus.render(new Versus(player1, null), Player.findAll()));
    }

    public static Result createVersus() {
        Http.Request request = request();
        Map<String, String[]> form = request.body().asFormUrlEncoded();
        Player player1 = Ebean.find(Player.class, Long.parseLong(form.get("player1")[0]));
        Player player2 = Ebean.find(Player.class, Long.parseLong(form.get("player2")[0]));
        return index(player1, player2);
    }

}
