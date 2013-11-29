import models.*;
import play.Application;
import play.GlobalSettings;
import play.mvc.Action;
import play.mvc.Http;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        if (Game.finder.all().isEmpty()) {
            initialize();
        }
    }

    private static void initialize() {
        towardSky();
        mushihime();
    }

    private static void mushihime() {
        Game game = new Game("Mushihimesama Futari", "http://ecx.images-amazon.com/images/I/51WQT7UfPJL.jpg", "http://forum.shmup.com/viewtopic.php?f=20&t=17554");
        game.save();
        for (String s : asList("Stage 1", "Stage 2", "Stage 3", "Stage 4", "Stage 5", "ALL")) {
            Stage stage = new Stage(s);
            stage.game = game;
            stage.save();
        }
        for (String s : asList("Original", "Maniac", "God", "Ultra")) {
            Difficulty difficulty = new Difficulty(s);
            difficulty.game = game;
            difficulty.save();
        }
        for (String s : asList("ARCADE", "XBOX 360", "MAME")) {
            Platform platform = new Platform(s);
            platform.game = game;
            platform.save();
        }
        for (String s : asList("Version 1.5", "Black Label", "Version arrange")) {
            Mode mode = new Mode(s);
            mode.game = game;
            mode.save();
        }
    }

    private static void towardSky() {
        Game game = new Game("Towards the sky : HOPE", "http://i73.servimg.com/u/f73/14/21/34/05/test0410.png", "http://forum.shmup.com/viewtopic.php?f=3&t=18684");
        game.save();
        for (String s : asList("Stage 1", "Stage 2", "Stage 3", "ALL")) {
            Stage stage = new Stage(s);
            stage.game = game;
            stage.save();
        }
        for (String s : asList("EASY", "NORMAL", "HARD")) {
            Difficulty difficulty = new Difficulty(s);
            difficulty.game = game;
            difficulty.save();
        }
        for (String s : asList("MEGADRIVE")) {
            Platform platform = new Platform(s);
            platform.game = game;
            platform.save();
        }
    }

    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        return new User(actionMethod);
    }

}