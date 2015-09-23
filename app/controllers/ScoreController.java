package controllers;

import actions.User;
import com.avaje.ebean.Ebean;
import com.google.common.base.Predicate;
import com.google.common.io.Files;
import models.Difficulty;
import models.Game;
import models.Mode;
import models.Platform;
import models.Player;
import models.Score;
import models.Ship;
import models.Stage;
import org.joda.time.DateTime;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.score_create;
import views.html.score_import;
import views.html.score_update;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.avaje.ebean.Ebean.find;
import static com.google.common.collect.Collections2.filter;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
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
        return ok(score_update.render(score, form(Score.class)));
    }

    public static Result save() throws IOException {
        if (!User.current().isAuthenticated()) {
            return unauthorized();
        }
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        Logger.info("Nouveau score envoyé par " + User.current().name + ", " + data.toString());
        models.Score score = createScore(data);
        if (score.value == null) {
            scoreForm.reject("Veuillez saisir une valeur de score.");
            return badRequest(views.html.score_create.render(score.game, scoreForm));
        }
        List<Http.MultipartFormData.FilePart> files = request().body().asMultipartFormData().getFiles();
        if (!files.isEmpty()) {
            if (files.get(0).getKey().equals("photo")) {
                storePhoto(score, files.get(0));
            } else {
                storeInp(score, files.get(0));
            }
            if (files.size() > 1) {
                storeInp(score, files.get(1));
            }
        }
        Score bestScore = score.player.getBestScoreFor(score.game, score.mode, score.difficulty);
        Integer oldRank = null;
        if (bestScore != null) {
            oldRank = bestScore.rank;
        }
        score.save();
        CacheController.getRankingCache().remove(score.game);
        CacheController.getSignatureCache().remove(score.player);
        CacheController.getMedalsCache().remove(score.player);
        score.game.recomputeRankings();
        score.refresh();

        if (oldRank != null && score.rank != null) {
            score.progression = oldRank - score.rank;
            score.update();
        }

        return shmup(score);
    }

    private static void storePhoto(Score score, Http.MultipartFormData.FilePart filePart) throws IOException {
        File file = filePart.getFile();
        String filename = filePart.getFilename().replaceAll("[^a-zA-Z0-9.]+", "");
        String pathname = "/photos/" + new Date().getTime() + "-" + filename;
        Files.copy(file, new File(pathname));
        score.photo = "http://hiscores.shmup.com" + pathname;
    }

    private static void storeInp(Score score, Http.MultipartFormData.FilePart filePart) throws IOException {
        File file = filePart.getFile();
        String filename = filePart.getFilename().replaceAll("[^a-zA-Z0-9.]+", "");
        String pathname = "/inp/" + new Date().getTime() + "-" + filename;
        Files.copy(file, new File(pathname));
        score.inp = "http://hiscores.shmup.com" + pathname;
    }

    public static Result update() throws IOException {
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        Logger.info("Mise a jour du score envoyé par " + User.current().name + ", " + data.toString());
        models.Score score = Ebean.find(models.Score.class, Long.valueOf(data.get("scoreId")));
        if (!score.isPlayedBy(User.current())) {
            return unauthorized();
        }
        updateScore(score, data);
        if (score.value == null) {
            scoreForm.reject("Veuillez saisir une valeur de score.");
            return badRequest(views.html.score_update.render(score, scoreForm));
        }
        List<Http.MultipartFormData.FilePart> files = request().body().asMultipartFormData().getFiles();
        if (!files.isEmpty()) {
            if (files.get(0).getKey().equals("photo")) {
                storePhoto(score, files.get(0));
            } else {
                storeInp(score, files.get(0));
            }
            if (files.size() > 1) {
                storeInp(score, files.get(1));
            }
        } else {
            score.photo = data.get("oldPhoto");
            score.inp = data.get("oldInp");
        }
        score.update();
        CacheController.getRankingCache().remove(score.game);
        CacheController.getSignatureCache().remove(score.player);
        CacheController.getMedalsCache().remove(score.player);
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
        Stage stage = stage(data);
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

    private static Stage stage(Map<String, String> data) {
        String stage = data.get("stage");
        if (stage == null) {
            return null;
        }
        return find(Stage.class, parseLong(stage));
    }

    private static void updateScore(models.Score score, Map<String, String> data) {
        score.stage = stage(data);
        score.mode = mode(data);
        score.difficulty = difficulty(data);
        score.ship = ship(data);
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

    public static BigDecimal value(Map<String, String> data) {
        String scoreValue = data.get("value");
        String minutes = data.get("minutes");
        String seconds = data.get("seconds");
        String milliseconds = data.get("milliseconds");
        if (isBlank(scoreValue) && isBlank(minutes) && isBlank(seconds) && isBlank(milliseconds)) {
            return null;
        }
        if (isNotBlank(scoreValue)) {
            StringBuilder strValue = new StringBuilder();
            for (Character c : scoreValue.toCharArray()) {
                if (isNumeric(c.toString())) {
                    strValue.append(c);
                }
            }
            return new BigDecimal(strValue.toString());
        } else {
            minutes = minutes.trim().isEmpty() ? "0" : minutes.trim();
            seconds = seconds.trim().isEmpty() ? "0" : seconds.trim();
            milliseconds = milliseconds.trim().isEmpty() ? "0" : milliseconds.trim();

            return BigDecimal.valueOf(new DateTime().withTimeAtStartOfDay().withDate(0, 1, 1).
                    withMinuteOfHour(parseInt(minutes)).
                    withSecondOfMinute(parseInt(seconds)).
                    withMillisOfSecond(parseInt(milliseconds)).
                    getMillis());
        }
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
                    current.gapWithPreviousScore = Math.abs(gap.multiply(BigDecimal.valueOf(100)).divide(previous.value, HALF_UP).longValue());
                }
            }
        }
        return scores;
    }

    public static Result importScore(Game game) throws IOException {
        if (!User.current().isAuthenticated()) {
            return unauthorized();
        }
        Form<models.Score> scoreForm = new Form<models.Score>(models.Score.class).bindFromRequest();
        Map<String, String> data = scoreForm.data();
        models.Score score = createScore(data, playerName(game, data));

        List<Http.MultipartFormData.FilePart> files = request().body().asMultipartFormData().getFiles();
        if (!files.isEmpty()) {
            storePhoto(score, files.get(0));
        }

        score.save();
        CacheController.getRankingCache().remove(game);
        CacheController.getSignatureCache().remove(score.player);
        CacheController.getMedalsCache().remove(score.player);
        game.recomputeRankings();
        return ok(score_import.render(game, form(Score.class)));
    }

    private static String playerName(Game game, Map<String, String> data) {
        if (game.event == null) {
            return data.get("player");
        }
        return game.event.name + " - " + data.get("player");
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
        CacheController.getRankingCache().remove(game);
        CacheController.getSignatureCache().remove(score.player);
        CacheController.getMedalsCache().remove(score.player);
        game.recomputeRankings();
        return ok(score_import.render(game, form(Score.class)));
    }

}
