package plugins;

import com.avaje.ebean.Ebean;
import models.Score;

import java.util.List;

public class Stats {

    public static long gameCount() {
        return Ebean.createQuery(models.Game.class).findRowCount();
    }

    public static long playerCount() {
        return Ebean.createQuery(models.Player.class).findRowCount();
    }

    public static long scoreCount() {
        return Ebean.createQuery(models.Score.class).findRowCount();
    }

}
