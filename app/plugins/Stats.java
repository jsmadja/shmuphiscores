package plugins;

import com.avaje.ebean.Ebean;
import models.Game;
import models.Player;
import models.Score;

public class Stats {

    public static long gameCount() {
        return Ebean.find(Game.class).findRowCount();
    }

    public static long playerCount() {
        return Ebean.find(Player.class).findRowCount();
    }

    public static long scoreCount() {
        return Ebean.find(Score.class).findRowCount();
    }

}
