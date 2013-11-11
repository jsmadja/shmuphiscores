package controllers;

import models.Player;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Map;

import static java.lang.Long.parseLong;

public class Score extends Controller {

    public static Result save() {
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        String login = data.get("login");
        String password = data.get("password");
        if (isAuthenticated(login, password)) {
            models.Score score = createScore(scoreForm, data, login);
            score.save();
            if("OUI".equalsIgnoreCase(data.get("post"))) {
                return shmup(score);
            } else {
                return redirect("/");
            }
        }
        return unauthorized();
    }

    public static Result shmup(models.Score score) {
        return ok(views.html.post_to_shmup.render(score));
    }

    private static models.Score createScore(Form<models.Score> scoreForm, Map<String, String> data, String login) {
        models.Score score = scoreForm.get();
        score.difficulty = score.difficulty.getFinder().byId(parseLong(score.difficulty.name));
        score.stage = score.stage.getFinder().byId(parseLong(score.stage.name));
        if(score.mode != null) {
            score.mode = score.mode.getFinder().byId(parseLong(score.mode.name));
        }
        score.platform = score.platform.getFinder().byId(parseLong(score.platform.name));
        score.game = models.Game.finder.byId(parseLong(data.get("gameId")));
        score.player = findOrCreatePlayer(login);
        return score;
    }

    private static boolean isAuthenticated(String login, String password) {
        return true;
    }

    private static Player findOrCreatePlayer(String login) {
        Player player = Player.finder.where()
                .eq("name", login)
                .findUnique();
        if (player == null) {
            player = new Player(login);
            player.save();
        }
        return player;
    }

}
