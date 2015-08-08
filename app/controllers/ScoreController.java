package controllers;

import actions.User;
import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import models.Difficulty;
import models.Game;
import models.Mode;
import models.Platform;
import models.Player;
import models.Score;
import models.Ship;
import models.Stage;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.score_create;
import views.html.score_import;
import views.html.score_update;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.avaje.ebean.Ebean.find;
import static com.google.common.collect.Collections2.filter;
import static java.lang.Long.parseLong;
import static java.math.RoundingMode.HALF_UP;
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

    public static Result importScores(Game game) {
        return ok(score_import.render(game, form(Score.class)));
    }

    public static Result read(models.Score score) {
        if (score == null) {
            return notFound();
        }
        return ok(score_update.render(score));
    }

    public static Result save() {
        if (!User.current().isAuthenticated()) {
            return unauthorized();
        }
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
        PlayerController.getSignatureCache().remove(score.player);
        PlayerController.getMedalsCache().remove(score.player);
        score.game.recomputeRankings();
        score.refresh();
        return shmup(score);
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
        PlayerController.getSignatureCache().remove(score.player);
        PlayerController.getMedalsCache().remove(score.player);
        score.game.recomputeRankings();
        return redirect("/");
    }

    public static Result shmup(models.Score score) {
        return ok(views.html.post_to_shmup.render(score));
    }

    private static models.Score createScore(Map<String, String> data) {
        String login = User.current().name;
        return createScore(data, login);
    }

    private static Score createScore(Map<String, String> data, String login) {
        Difficulty difficulty = difficulty(data);
        Stage stage = find(Stage.class, parseLong(data.get("stage")));
        Ship ship = ship(data);
        Mode mode = mode(data);
        Platform platform = find(Platform.class, parseLong(data.get("platform")));
        Player player = Player.findOrCreatePlayer(login);
        Game game = find(Game.class, parseLong(data.get("gameId")));
        BigDecimal value = value(data);
        String comment = data.get("comment");
        String replay = data.get("replay");
        String photo = data.get("photo");
        return new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
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

    private static BigDecimal value(Map<String, String> data) {
        String scoreValue = data.get("value");
        StringBuilder strValue = new StringBuilder();
        for (Character c : scoreValue.toCharArray()) {
            if (isNumeric(c.toString())) {
                strValue.append(c);
            }
        }
        return new BigDecimal(strValue.toString());
    }

    private static Mode mode(Map<String, String> data) {
        Mode mode = null;
        if (data.get("mode") != null) {
            mode = find(Mode.class, parseLong(data.get("mode")));
        }
        return mode;
    }

    private static Ship ship(Map<String, String> data) {
        Ship ship = null;
        if (data.get("ship") != null) {
            ship = find(Ship.class, parseLong(data.get("ship")));
        }
        return ship;
    }

    public static Collection<Score> findProgressionOf(final Score score) {
        List<Score> scores = score.player.allScores;
        scores = new ArrayList<Score>(filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score _score) {
                return score.game.equals(_score.game) && score.mode == _score.mode && score.difficulty == _score.difficulty;
            }
        }));
        if (scores.size() > 1) {
            for (int i = 1; i < scores.size(); i++) {
                Score previous = scores.get(i - 1);
                Score current = scores.get(i);
                BigDecimal gap = current.value.subtract(previous.value);
                if (previous.value.equals(BigDecimal.ZERO)) {
                    current.gapWithPreviousScore = 0L;
                } else {
                    current.gapWithPreviousScore = gap.multiply(BigDecimal.valueOf(100)).divide(previous.value, HALF_UP).longValue();
                }
            }
        }
        return scores;
    }

    public static Result importScore(Game game) {
        if (!User.current().isAuthenticated()) {
            return unauthorized();
        }
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        models.Score score = createScore(data, data.get("player"));
        score.save();
        RankingController.getRankingCache().remove(game);
        PlayerController.getSignatureCache().remove(score.player);
        PlayerController.getMedalsCache().remove(score.player);
        game.recomputeRankings();
        return ok(score_import.render(game, form(Score.class)));
    }

    public static Result delete() {
        if (!User.current().isAuthenticated()) {
            return unauthorized();
        }
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        models.Score score = Score.finder.byId(Long.parseLong(data.get("score")));
        score.delete();
        Game game = score.game;
        RankingController.getRankingCache().remove(game);
        PlayerController.getSignatureCache().remove(score.player);
        PlayerController.getMedalsCache().remove(score.player);
        game.recomputeRankings();
        return ok(score_import.render(game, form(Score.class)));
    }

}
