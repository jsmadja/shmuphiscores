package controllers;

import com.google.common.base.Joiner;
import com.sun.syndication.io.FeedException;
import models.Game;
import models.Player;
import models.Timeline;
import models.Versus;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationController extends Controller {

    public static Result index() {
        return ok(index.render(new Timeline()));
    }

    private static void recompute() {
        List<Game> all = Game.findAll();
        for (Game game : all) {
            game.recomputeRankings();
        }
    }

    private static void reverseVersus() {
        Map<Player, List<Player>> counts = new HashMap<Player, List<Player>>();

        List<Player> all = Player.findAll();
        for (Player player1 : all) {
            Versus bestVersus = player1.getBestVersus();
            Player player2 = bestVersus.player2;
            List<Player> opponents = counts.get(player2);
            if (opponents == null) {
                opponents = new ArrayList<Player>();
                counts.put(player2, opponents);
            }
            if (!player1.scores.isEmpty()) {
                opponents.add(player1);
            }
        }

        List<Map.Entry<Player, List<Player>>> list = new ArrayList<Map.Entry<Player, List<Player>>>(counts.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Player, List<Player>>>() {
            @Override
            public int compare(Map.Entry<Player, List<Player>> o1, Map.Entry<Player, List<Player>> o2) {
                return o2.getValue().size() - o1.getValue().size();
            }
        });

        for (Map.Entry<Player, List<Player>> playerIntegerEntry : list) {
            System.err.println(playerIntegerEntry.getKey().name + " -> " + playerIntegerEntry.getValue().size() + " " + Joiner.on(", ").join(playerIntegerEntry.getValue()));
        }
    }

    public static Result indexRss() throws IOException, FeedException {
        return ok(new Timeline().rss());
    }

}
