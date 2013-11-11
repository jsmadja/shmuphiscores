package controllers;

import com.avaje.ebean.Ebean;
import models.Game;
import models.Score;
import models.Timeline;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

public class Application extends Controller {

    public static Result index() {
        Timeline timeline = new Timeline();
        List<Score> scores = Ebean.createQuery(Score.class).orderBy("createdAt desc").setMaxRows(10).findList();
        for (Score score : scores) {
            timeline.scores.add(score);
        }
        return ok(index.render(timeline));
    }

    public static Result selectGame() {
        return ok(views.html.select_game.render(models.Game.findAll()));
    }

    public static Result fillForm() {
        models.Game game = Game.finder.byId(Long.parseLong(request().body().asFormUrlEncoded().get("game")[0]));
        return ok(views.html.scoreform.render(game));
    }

}
