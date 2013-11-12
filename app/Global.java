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
        towardSky();
        mushihime();

        Player sl = new Player("-S.L-");
        sl.save();

        Player anzymus = new Player("anzymus");
        anzymus.save();

        Game viewpoint = Game.finder.byId(2L);
        models.Score score = new models.Score(viewpoint, sl, viewpoint.stages.get(3), null, viewpoint.difficulties.get(3), "ALL CLEAR! WESTERN RECORD", viewpoint.platforms.get(0), 3300750L, "http://img854.imageshack.us/img854/7086/img1627h.jpg");
        score.save();
        viewpoint.scores.add(score);
        viewpoint.update();

        Game akaiKatana = Game.finder.byId(1L);
        score = new models.Score(akaiKatana, anzymus, akaiKatana.stages.get(0), akaiKatana.modes.get(0), akaiKatana.difficulties.get(0), "Mon premier score!", akaiKatana.platforms.get(0), 12456543L, null);
        score.save();
        akaiKatana.scores.add(score);
        akaiKatana.update();

        Game towards = Game.finder.byId(3L);
        score = new Score(towards, anzymus, towards.stages.get(0), null, towards.difficulties.get(0), "Un score pour un jeu imaginaire", towards.platforms.get(0), 2543L, null);
        score.save();
        towards.scores.add(score);
        towards.update();
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