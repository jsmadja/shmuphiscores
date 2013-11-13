package controllers;

import com.avaje.ebean.Ebean;
import models.Difficulty;
import models.Mode;
import models.Platform;
import models.Stage;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Map;

import static com.avaje.ebean.Ebean.find;
import static java.lang.Long.parseLong;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import views.html.score_update;

public class Score extends Controller {

    public static Result read(models.Score score) {
        return ok(score_update.render(score));
    }

    public static Result save() {
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        String login = data.get("login");
        String password = data.get("password");
        if (isAuthenticated(login, password)) {
            models.Score score = createScore(data);
            score.save();
            if ("OUI".equalsIgnoreCase(data.get("post"))) {
                return shmup(score);
            } else {
                return redirect("/");
            }
        }
        return unauthorized();
    }

    public static Result update() {
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        models.Score score = Ebean.find(models.Score.class, Long.valueOf(data.get("scoreId")));
        String login = score.player.name;
        String password = data.get("password");
        if (isAuthenticated(login, password)) {
            updateScore(score, data);
            score.update();
            if ("OUI".equalsIgnoreCase(data.get("post"))) {
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

    private static models.Score createScore(Map<String, String> data) {
        Difficulty difficulty = find(Difficulty.class, parseLong(data.get("difficulty")));
        Stage stage = find(Stage.class, parseLong(data.get("stage")));
        Mode mode = mode(data);
        Platform platform = find(Platform.class, parseLong(data.get("platform")));
        models.Player player = models.Player.findOrCreatePlayer(data.get("login"));
        models.Game game = find(models.Game.class, parseLong(data.get("gameId")));
        Long value = value(data);
        String comment = data.get("comment");
        String photo = data.get("photo");
        return new models.Score(game, player, stage, mode, difficulty, comment, platform, value, photo);
    }

    private static void updateScore(models.Score score, Map<String, String> data) {
        score.stage = find(Stage.class, parseLong(data.get("stage")));
        score.mode = mode(data);
        score.difficulty = find(Difficulty.class, parseLong(data.get("difficulty")));
        score.comment = data.get("comment");
        score.platform = find(Platform.class, parseLong(data.get("platform")));
        score.value = value(data);
        score.photo = data.get("photo");
    }

    private static Long value(Map<String, String> data) {
        String scoreValue = data.get("value");
        StringBuilder strValue = new StringBuilder();
        for (Character c : scoreValue.toCharArray()) {
            if (isNumeric(c.toString())) {
                strValue.append(c);
            }
        }
        Long value = Long.valueOf(strValue.toString());
        if (value < 0) {
            value *= -1;
        }
        return value;
    }

    private static Mode mode(Map<String, String> data) {
        Mode mode = null;
        if (data.get("mode") != null) {
            mode = find(Mode.class, parseLong(data.get("mode")));
        }
        return mode;
    }

    private static boolean isAuthenticated(String login, String password) {
        return true;
    }

}
