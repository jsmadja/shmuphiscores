package controllers;

import com.avaje.ebean.Ebean;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import models.Score;
import models.Timeline;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ApplicationController extends Controller {

    public static Result index() {
        return ok(index.render(createTimeLine()));
    }

    public static Result indexRss() throws IOException, FeedException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("hiscores.shmup.com");
        feed.setFeedType("rss_2.0");
        feed.setDescription("hiscores.shmup.com");
        feed.setLink("http://hiscores.shmup.com");
        Timeline timeline = createTimeLine();

        List entries = new ArrayList();
        for (Score score : timeline.scores) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setAuthor(score.player.name);
            entry.setLink("http://hiscores.shmup.com/score/" + score.id);
            SyndContentImpl syndContent = new SyndContentImpl();
            syndContent.setValue(score.getGameTitle() + " " + score.formattedValue() + " pts");
            entry.setDescription(syndContent);
            entry.setUpdatedDate(score.getCreatedAt());
            entries.add(entry);
        }
        feed.setEntries(entries);

        Writer writer = new StringWriter();
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, writer);
        writer.close();
        response().setContentType("application/rss+xml");
        return ok(writer.toString());
    }

    private static Timeline createTimeLine() {
        Timeline timeline = new Timeline();
        List<Score> scores = Ebean.createQuery(Score.class).orderBy("createdAt desc").setMaxRows(10).findList();
        for (Score score : scores) {
            timeline.scores.add(score);
        }
        return timeline;
    }

}
