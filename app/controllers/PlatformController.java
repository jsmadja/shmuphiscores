package controllers;

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
        return Game.finder.where().ieq("platforms.name", platformName).order("title").findList();
    }

}
