package controllers;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import models.Game;
import models.Platform;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.platform_read;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
        List<Game> games = Game.findAll();
        games = new ArrayList<Game>(Collections2.filter(games, new Predicate<Game>() {
            @Override
            public boolean apply(@Nullable Game game) {
                List<Platform> platforms = game.platforms;
                for (Platform platform : platforms) {
                    if (platform.name.equalsIgnoreCase(platformName)) {
                        return true;
                    }
                }
                return false;
            }
        }));
        return games;
    }

}
