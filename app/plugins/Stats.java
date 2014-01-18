package plugins;

import models.Game;
import models.Player;
import models.Score;

public class Stats {

    public static long gameCount() {
        return Game.finder.findRowCount();
    }

    public static long playerCount() {
        return Player.finder.findRowCount();
    }

    public static long scoreCount() {
        return Score.finder.findRowCount();
    }

}
