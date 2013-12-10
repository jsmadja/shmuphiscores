package controllers;

import com.avaje.ebean.Ebean;
import models.Game;
import models.Score;
import models.Timeline;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.score_create;

import java.util.List;

import static play.data.Form.form;

public class ApplicationController extends Controller {

    public static Result index() {
        Timeline timeline = new Timeline();
        List<Score> scores = Ebean.createQuery(Score.class).orderBy("createdAt desc").setMaxRows(10).findList();
        for (Score score : scores) {
            timeline.scores.add(score);
        }
        return ok(index.render(timeline));
    }

}
