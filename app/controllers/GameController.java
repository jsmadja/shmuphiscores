package controllers;

import actions.User;
import com.avaje.ebean.Ebean;
import models.Difficulty;
import models.Event;
import models.Game;
import models.Mode;
import models.Platform;
import models.Ship;
import models.Stage;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.game_read;
import views.html.game_update;

import java.util.Map;
import java.util.Scanner;

import static com.avaje.ebean.Ebean.find;
import static java.lang.Long.parseLong;
import static play.data.Form.form;

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

    public static Result update(models.Game game) {
        if (game == null) {
            return notFound();
        }
        if (!User.current().isAuthenticated() && !User.current().isAdministrator()) {
            return unauthorized();
        }
        return ok(game_update.render(game));
    }

    public static Result create() {
        return ok(views.html.game_create.render());
    }

    public static Result createEventOf(models.Game game) {
        return ok(views.html.event_create.render(game, form(Event.class)));
    }

    public static Result saveEvent(models.Game originalGame) {
        Form<models.Event> form = new Form<Event>(models.Event.class).bindFromRequest();

        models.Event event = form.get();

        Game game = new Game("[" + event.name + "] " + originalGame.title, originalGame.cover, originalGame.thread);
        if(originalGame.hasDifficulties()) {
            for (Difficulty difficulty : originalGame.difficulties) {
                game.difficulties.add(new Difficulty(difficulty.name));
            }
        }
        if(originalGame.hasStages()) {
            for (Stage stage : originalGame.stages) {
                game.stages.add(new Stage(stage.name));
            }
        }
        if(originalGame.hasModes()) {
            for (Mode mode : originalGame.modes) {
                game.modes.add(new Mode(mode.name, mode.scoreType));
            }
        }
        if(originalGame.hasPlatforms()) {
            for (Platform platform : originalGame.platforms) {
                game.platforms.add(new Platform(platform.name));
            }
        }
        if(originalGame.hasShip()) {
            for(Ship ship:originalGame.ships) {
                game.ships.add(new Ship(ship.name));
            }
        }
        event.game = game;
        Ebean.save(event);
        return index(game);
    }

    public static Result save() {
        Form<models.Game> form = new Form<models.Game>(models.Game.class).bindFromRequest();
        models.Game game = form.get();
        game.save();
        createPlatforms(game);
        createDifficulties(game);
        createShips(game);
        createModes(game);
        createStages(game);
        return index(game);
    }

    public static Result addPlatform(Game game) {
        String platformName = request().body().asFormUrlEncoded().get("platform")[0];
        createPlatform(game, platformName);
        return update(game);
    }

    public static Result addDifficulty(Game game) {
        Map<String, String[]> data = request().body().asFormUrlEncoded();
        String difficultyName = data.get("difficulty")[0];
        int sortOrder = Integer.parseInt(data.get("sortOrder")[0]);
        createDifficulty(game, sortOrder, difficultyName);
        return update(game);
    }

    public static Result addMode(Game game) {
        Map<String, String[]> data = request().body().asFormUrlEncoded();
        String modeName = data.get("mode")[0];
        int sortOrder = Integer.parseInt(data.get("sortOrder")[0]);
        createMode(game, sortOrder, modeName);
        return update(game);
    }

    public static Result addShip(Game game) {
        Map<String, String[]> data = request().body().asFormUrlEncoded();
        String shipName = data.get("ship")[0];
        int sortOrder = Integer.parseInt(data.get("sortOrder")[0]);
        createShip(game, sortOrder, shipName);
        return update(game);
    }

    public static Result addStage(Game game) {
        Map<String, String[]> data = request().body().asFormUrlEncoded();
        String stageName = data.get("stage")[0];
        int sortOrder = Integer.parseInt(data.get("sortOrder")[0]);
        createStage(game, sortOrder, stageName);
        return update(game);
    }

    private static void createPlatform(Game game, String platformName) {
        Platform platform = new Platform(platformName);
        platform.game = game;
        platform.save();
    }

    private static void createPlatforms(models.Game game) {
        Scanner sc = new Scanner(game.platforms.get(0).name);
        if (sc.hasNextLine()) {
            while (sc.hasNextLine()) {
                createPlatform(game, sc.nextLine().trim());
            }
        } else {
            createPlatform(game, " ");
        }
    }

    private static void createDifficulties(models.Game game) {
        Integer index = 0;
        Scanner sc = new Scanner(game.difficulties.get(0).name);
        while (sc.hasNextLine()) {
            createDifficulty(game, index, sc.nextLine().trim());
            index++;
        }
    }

    private static void createDifficulty(Game game, Integer index, String difficultyName) {
        Difficulty difficulty = new Difficulty(difficultyName);
        difficulty.game = game;
        difficulty.sortOrder = index.toString();
        difficulty.save();
    }

    private static void createModes(models.Game game) {
        Integer index = 0;
        Scanner sc = new Scanner(game.modes.get(0).name);
        while (sc.hasNextLine()) {
            createMode(game, index, sc.nextLine().trim());
            index++;
        }
    }

    private static void createMode(Game game, Integer index, String modeName) {
        Mode mode = new Mode(modeName);
        mode.game = game;
        mode.sortOrder = index.toString();
        mode.save();
    }

    private static void createShips(models.Game game) {
        Integer index = 0;
        Scanner sc = new Scanner(game.ships.get(0).name);
        while (sc.hasNextLine()) {
            createShip(game, index, sc.nextLine().trim());
            index++;
        }
    }

    private static void createShip(Game game, Integer index, String shipName) {
        Ship ship = new Ship(shipName);
        ship.game = game;
        ship.sortOrder = index.toString();
        ship.save();
    }

    private static void createStage(Game game, Integer index, String stageName) {
        Stage stage = new Stage(stageName);
        stage.game = game;
        stage.sortOrder = index.longValue();
        stage.save();
    }

    private static void createStages(models.Game game) {
        Long index = 0L;
        Scanner sc = new Scanner(game.stages.get(0).name);
        if (sc.hasNextLine()) {
            while (sc.hasNextLine()) {
                Stage stage = new Stage(sc.nextLine().trim());
                stage.game = game;
                stage.sortOrder = index;
                stage.save();
                index++;
            }
        } else {
            Stage stage = new Stage(" ");
            stage.game = game;
            stage.sortOrder = index;
            stage.save();
        }

    }

}
