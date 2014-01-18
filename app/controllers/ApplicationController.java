package controllers;

import com.avaje.ebean.Ebean;
import com.sun.syndication.io.FeedException;
import models.Score;
import models.Timeline;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.io.IOException;
import java.util.List;

public class ApplicationController extends Controller {

    public static Result index() {
        return ok(index.render(Timeline.createTimeLine()));
    }

    public static Result indexRss() throws IOException, FeedException {
        return ok(Timeline.createTimeLine().rss());
    }

}
