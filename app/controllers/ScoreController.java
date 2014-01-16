package controllers;

import actions.User;
import com.avaje.ebean.Ebean;
import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.score_create;
import views.html.score_update;

import java.util.List;
import java.util.Map;

import static com.avaje.ebean.Ebean.find;
import static java.lang.Long.parseLong;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static play.data.Form.form;

public class ScoreController extends Controller {

    public static Result selectGame() {
        return ok(views.html.select_game.render(Game.findAll()));
    }

    public static Result fillForm() {
        models.Game game = Game.finder.byId(Long.parseLong(request().body().asFormUrlEncoded().get("game")[0]));
        return ok(score_create.render(game, form(Score.class)));
    }

    public static Result fillFormWithGame(Game game) {
        return ok(score_create.render(game, form(Score.class)));
    }

    public static Result read(models.Score score) {
        if (score == null) {
            return notFound();
        }
        return ok(score_update.render(score));
    }

    public static Result save() {
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        Logger.info("Nouveau score envoyé par " + User.current().name + ", " + data.toString());
        models.Score score = createScore(data);
        if (score.isWorstThanOlders()) {
            scoreForm.reject("Score inférieur à un score déjà présent dans la base.");
            return badRequest(views.html.score_create.render(score.game, scoreForm));
        }
        score.save();
        RankingController.getRankingCache().remove(score.game);
        score.game.clearRankings();

        if ("OUI".equalsIgnoreCase(data.get("post"))) {
            return shmup(score);
        }
        return redirect("/");
    }

    public static Result update() {
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        Logger.info("Mise a jour du score envoyé par " + User.current().name + ", " + data.toString());
        models.Score score = Ebean.find(models.Score.class, Long.valueOf(data.get("scoreId")));
        if (!score.isPlayedBy(User.current())) {
            return unauthorized();
        }
        updateScore(score, data);
        score.update();
        RankingController.getRankingCache().remove(score.game);
        score.game.clearRankings();

        if ("OUI".equalsIgnoreCase(data.get("post"))) {
            return shmup(score);
        } else {
            return redirect("/");
        }
    }

    public static Result shmup(models.Score score) {
        return ok(views.html.post_to_shmup.render(score));
    }

    private static models.Score createScore(Map<String, String> data) {
        String login = User.current().name;
        Difficulty difficulty = difficulty(data);
        Stage stage = find(Stage.class, parseLong(data.get("stage")));
        Mode mode = mode(data);
        Platform platform = find(Platform.class, parseLong(data.get("platform")));
        models.Player player = models.Player.findOrCreatePlayer(login);
        models.Game game = find(models.Game.class, parseLong(data.get("gameId")));
        Long value = value(data);
        String comment = data.get("comment");
        String replay = data.get("replay");
        String photo = data.get("photo");
        return new models.Score(game, player, stage, mode, difficulty, comment, platform, value, photo, replay);
    }

    private static void updateScore(models.Score score, Map<String, String> data) {
        score.stage = find(Stage.class, parseLong(data.get("stage")));
        score.mode = mode(data);
        score.difficulty = difficulty(data);
        score.comment = data.get("comment");
        score.platform = find(Platform.class, parseLong(data.get("platform")));
        score.value = value(data);
        score.photo = data.get("photo");
        score.replay = data.get("replay");
    }

    private static Difficulty difficulty(Map<String, String> data) {
        Difficulty difficulty = null;
        if (data.get("difficulty") != null) {
            difficulty = find(Difficulty.class, parseLong(data.get("difficulty")));
        }
        return difficulty;
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

}
