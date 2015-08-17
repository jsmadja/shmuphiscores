package controllers;

import com.sun.syndication.io.FeedException;
import models.Timeline;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.io.IOException;

public class ApplicationController extends Controller {

    public static Result index() {
        return ok(index.render(new Timeline()));
    }

    public static Result indexRss() throws IOException, FeedException {
        return ok(new Timeline().rss());
    }

}
