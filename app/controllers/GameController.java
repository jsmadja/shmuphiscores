package controllers;

import models.Difficulty;
import models.Mode;
import models.Platform;
import models.Stage;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.game_read;

import java.util.Scanner;

public class GameController extends Controller {

    public static Result indexWithName(models.Game game, String name) {
        return ok(game_read.render(game));
    }

    public static Result index(models.Game game) {
        if (game == null) {
            return notFound();
        }
        return ok(game_read.render(game));
    }

    public static Result create() {
        return ok(views.html.game_create.render());
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
            Platform platform = new Platform(sc.nextLine().trim());
            platform.game = game;
            platform.save();
        }
    }

    private static void createDifficulties(models.Game game) {
        Integer index = 0;
        Scanner sc = new Scanner(game.difficulties.get(0).name);
        while (sc.hasNextLine()) {
            Difficulty difficulty = new Difficulty(sc.nextLine().trim());
            difficulty.game = game;
            difficulty.sortOrder = index.toString();
            difficulty.save();
            index++;
        }
    }

    private static void createModes(models.Game game) {
        Integer index = 0;
        Scanner sc = new Scanner(game.modes.get(0).name);
        while (sc.hasNextLine()) {
            Mode mode = new Mode(sc.nextLine().trim());
            mode.game = game;
            mode.sortOrder = index.toString();
            mode.save();
            index++;
        }
    }

    private static void createStages(models.Game game) {
        Long index = 0L;
        Scanner sc = new Scanner(game.stages.get(0).name);
        while (sc.hasNextLine()) {
            Stage stage = new Stage(sc.nextLine().trim());
            stage.game = game;
            stage.sortOrder = index;
            stage.save();
            index++;
        }
    }

}
