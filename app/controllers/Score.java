package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.score;

public class Score extends Controller {

    public static Result index() {
        return ok(score.render());
    }

}
