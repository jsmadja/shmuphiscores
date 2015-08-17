package controllers;

import com.avaje.ebean.Ebean;
import models.Game;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.platform_read;

import java.util.List;

public class PlatformController extends Controller {

    public static Result read(final String platformName) {
        if (platformName == null) {
            return notFound();
        }
        List<Game> games = getGamesByPlatform(platformName);
        return ok(platform_read.render(platformName, games));
    }

    public static List<Game> getGamesByPlatform(final String platformName) {
        return Ebean.find(Game.class).where().ieq("platforms.name", platformName).order("title").
                fetch("oneccs").
                fetch("platforms").
                findList();
    }

}
