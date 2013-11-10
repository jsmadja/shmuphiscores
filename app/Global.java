import models.*;
import play.Application;
import play.GlobalSettings;
import play.mvc.Action;
import play.mvc.Http;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        if(Game.finder.all().isEmpty()) {
            initialize();
        }
    }

    private static void initialize() {
        akaiKatana();
        viewpoint();
    }

    private static void akaiKatana() {
        Game game = new Game("Akai katana shin", "http://image.jeuxvideo.com/images/jaquettes/00040107/jaquette-akai-katana-xbox-360-cover-avant-g-1336727907.jpg", "http://forum.shmup.com/viewtopic.php?f=20&t=16210");
        game.save();
        for (String s : asList("Stage 1", "Stage 2", "Stage 3")) {
            Stage stage = new Stage(s);
            stage.game = game;
            stage.save();
        }
        for (String s : asList("Shin", "Origin", "Climax")) {
            Mode mode = new Mode(s);
            mode.game = game;
            mode.save();
        }
        for (String s : asList("NOVICE", "EXPERT")) {
            Difficulty difficulty = new Difficulty(s);
            difficulty.game = game;
            difficulty.save();
        }
        for (String s : asList("Xbox360", "PS3", "ARCADE")) {
            Platform platform = new Platform(s);
            platform.game = game;
            platform.save();
        }
    }

    private static void viewpoint() {
        Game game = new Game("Viewpoint", "http://image.gamespotcdn.net/gamespot/images/box/6/4/8/565648_5616_front.jpg", "http://forum.shmup.com/viewtopic.php?f=20&t=10468");
        game.save();
        for (String s : asList("Stage 1", "Stage 2", "Stage 3", "Stage 4")) {
            Stage stage = new Stage(s);
            stage.game = game;
            stage.save();
        }
        for (String s : asList("EASY", "NORMAL", "MVS", "HARD")) {
            Difficulty difficulty = new Difficulty(s);
            difficulty.game = game;
            difficulty.save();
        }
        for (String s : asList("NEOGEO", "NEOGEO CD", "PS1")) {
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