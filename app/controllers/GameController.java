package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebeaninternal.server.text.csv.CsvUtilReader;
import models.Difficulty;
import models.Mode;
import models.Platform;
import models.Stage;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import plugins.ScoreImporter;
import scala.collection.immutable.List;
import views.html.game_read;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Scanner;

public class GameController extends Controller {

    public static Result indexWithName(models.Game game, String name) {
        return ok(game_read.render(game));
    }

    public static Result index(models.Game game) {
        if(game == null) {
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

    public static Result importScores() {
        return ok(views.html.import_scores.render(models.Game.findAll()));
    }

    public static Result saveScores() throws IOException {
        Map<String, String[]> data = request().body().asFormUrlEncoded();
        String gameId = data.get("game")[0];
        String csv = data.get("scores")[0];
        models.Game game = Ebean.find(models.Game.class, Long.valueOf(gameId));
        ScoreImporter.importScores(game, new CsvUtilReader(new StringReader(csv), ';').readAll());
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
