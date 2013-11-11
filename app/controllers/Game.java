package controllers;

import models.Difficulty;
import models.Mode;
import models.Platform;
import models.Stage;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Scanner;

public class Game extends Controller {

    public static Result index(models.Game game) {
        return ok(views.html.game.render(game));
    }

    public static Result create() {
        return ok(views.html.new_game.render());
    }

    public static Result save() {
        Form<models.Game> form = new Form<models.Game>(models.Game.class).bindFromRequest();
        models.Game game = form.get();
        game.save();
        createPlatforms(game);
        createDifficulties(game);
        createModes(game);
        createStages(game);
        return index(game);
    }

    private static void createPlatforms(models.Game game) {
        Scanner sc = new Scanner(game.platforms.get(0).name);
        while (sc.hasNextLine()) {
            Platform platform = new Platform(sc.nextLine());
            platform.game = game;
            platform.save();
        }
    }

    private static void createDifficulties(models.Game game) {
        Scanner sc = new Scanner(game.difficulties.get(0).name);
        while (sc.hasNextLine()) {
            Difficulty difficulty = new Difficulty(sc.nextLine());
            difficulty.game = game;
            difficulty.save();
        }
    }

    private static void createModes(models.Game game) {
        Scanner sc = new Scanner(game.modes.get(0).name);
        while (sc.hasNextLine()) {
            Mode mode = new Mode(sc.nextLine());
            mode.game = game;
            mode.save();
        }
    }

    private static void createStages(models.Game game) {
        Scanner sc = new Scanner(game.stages.get(0).name);
        while (sc.hasNextLine()) {
            Stage stage = new Stage(sc.nextLine());
            stage.game = game;
            stage.save();
        }
    }

}
