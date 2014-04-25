package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicate;
import models.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.avaje.ebean.Ebean.find;
import static com.google.common.collect.Collections2.filter;
import static java.util.Collections.sort;

public class MobileController extends Controller {

    public static Result games() {
        List<Game> games = Game.finder.all();
        sort(games);
        ArrayNode result = Json.newObject().arrayNode();
        for (Game game : games) {
            result.add(Json.newObject().put("value", game.id).put("label", game.title));
        }
        return ok(result);
    }

    public static Result score() {
        Score score = createScore(request().body().asJson());
        score.save();
        return ok("http://hiscores.shmup.com/score/shmup/" + score.id);
    }

    private static models.Score createScore(JsonNode data) {
        Difficulty difficulty = difficulty(data.get("difficulty"));
        Stage stage = find(Stage.class, data.get("stage").asLong());
        Mode mode = mode(data.get("mode"));
        Platform platform = find(Platform.class, data.get("platform").asLong());
        models.Player player = Player.finder.byId(data.get("player").asLong());
        models.Game game = find(models.Game.class, data.get("game").asLong());
        Long value = data.get("score").asLong();
        String comment = data.get("precision").asText();
        String photoBase64 = data.get("photo") == null ? null : data.get("photo").asText();
        return new Score(game, player, stage, mode, difficulty, comment, platform, value, photoBase64);
    }

    private static Difficulty difficulty(JsonNode data) {
        Difficulty difficulty = null;
        if (data != null) {
            difficulty = find(Difficulty.class, data.asLong());
        }
        return difficulty;
    }

    private static Mode mode(JsonNode data) {
        Mode mode = null;
        if (data != null) {
            mode = find(Mode.class, data.asLong());
        }
        return mode;
    }

    public static Result players() {
        List<Player> players = Player.finder.all();
        players = new ArrayList<Player>(filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(@Nullable Player player) {
                return player.shmupUserId != null;
            }
        }));
        sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player, Player player2) {
                return player.name.compareToIgnoreCase(player2.name);
            }
        });
        ArrayNode result = Json.newObject().arrayNode();
        for (Player player : players) {
            result.add(Json.newObject().put("value", player.id).put("label", player.name));
        }
        return ok(result);
    }

    public static Result game(Game game) {
        ObjectNode gameNode = Json.newObject();
        insertStages(game, gameNode);
        insertModes(game, gameNode);
        insertDifficulties(game, gameNode);
        insertPlatforms(game, gameNode);
        return ok(gameNode);
    }

    private static void insertStages(Game game, ObjectNode gameNode) {
        ArrayNode stagesNode = Json.newObject().arrayNode();
        List<Stage> stages = game.stages;
        Collections.sort(stages, new Comparator<Stage>() {
            @Override
            public int compare(Stage stage, Stage stage2) {
                return stage.sortOrder.compareTo(stage2.sortOrder);
            }
        });
        for (Stage item : stages) {
            stagesNode.add(Json.newObject().put("value", item.id).put("label", item.name));
        }
        gameNode.put("stages", stagesNode);
    }

    private static void insertModes(Game game, ObjectNode gameNode) {
        ArrayNode modesNode = Json.newObject().arrayNode();
        List<Mode> modes = game.modes;
        Collections.sort(modes, new Comparator<Mode>() {
            @Override
            public int compare(Mode mode, Mode mode2) {
                return mode.sortOrder.compareTo(mode2.sortOrder);
            }
        });
        for (Mode item : modes) {
            modesNode.add(Json.newObject().put("value", item.id).put("label", item.name));
        }
        gameNode.put("modes", modesNode);
    }

    private static void insertDifficulties(Game game, ObjectNode gameNode) {
        ArrayNode difficultiesNode = Json.newObject().arrayNode();
        List<Difficulty> difficulties = game.difficulties;
        Collections.sort(difficulties, new Comparator<Difficulty>() {
            @Override
            public int compare(Difficulty difficulty, Difficulty difficulty2) {
                return difficulty.sortOrder.compareTo(difficulty2.sortOrder);
            }
        });
        for (Difficulty item : difficulties) {
            difficultiesNode.add(Json.newObject().put("value", item.id).put("label", item.name));
        }
        gameNode.put("difficulties", difficultiesNode);
    }

    private static void insertPlatforms(Game game, ObjectNode gameNode) {
        ArrayNode platformsNode = Json.newObject().arrayNode();
        Collections.sort(game.platforms, new Comparator<Platform>() {
            @Override
            public int compare(Platform platform, Platform platform2) {
                return platform.name.compareToIgnoreCase(platform2.name);
            }
        });
        for (Platform item : game.platforms) {
            platformsNode.add(Json.newObject().put("value", item.id).put("label", item.name));
        }
        gameNode.put("platforms", platformsNode);
    }

}
