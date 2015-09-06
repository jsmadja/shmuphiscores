package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import models.Game;
import models.Timeline;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class ApiController extends Controller {

    public static Result timeline() {
        response().setContentType("application/json");
        return ok(new Timeline().json());
    }

    public static Result games() {
        response().setContentType("application/json");
        ArrayNode nodes = new ArrayNode(JsonNodeFactory.instance);
        List<Game> games = Game.findAll();
        for (Game game : games) {
            nodes.add(game.json());
        }
        return ok(nodes);
    }

    public static Result gameDetail(Game game) {
        response().setContentType("application/json");
        return ok(game.jsonDetail());
    }

}
