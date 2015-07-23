package models;

import com.avaje.ebean.Ebean;
import com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import play.mvc.Controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Timeline {

    public Collection<Score> scores = new ArrayList<Score>();

    public static Timeline createTimeLine() {
        Timeline timeline = new Timeline();
        List<Score> scores = Ebean.createQuery(Score.class).orderBy("createdAt desc").setMaxRows(50).findList();
        for (Score score : scores) {
            if (score.hasRank()) {
                timeline.scores.add(score);

            }
        }
        return timeline;
    }

    public String rss() throws IOException, FeedException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("hiscores.shmup.com");
        feed.setFeedType("rss_2.0");
        feed.setDescription("hiscores.shmup.com");
        feed.setLink("http://hiscores.shmup.com");
        List entries = new ArrayList();
        feed.setEntries(entries);
        for (Score score : scores) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(score.getGameTitle());
            entry.setAuthor(score.player.name);
            entry.setLink("http://hiscores.shmup.com/score/" + score.id);
            SyndContentImpl content = new SyndContentImpl();
            content.setValue(score.tweet());
            entry.setDescription(content);
            entry.setPublishedDate(score.getCreatedAt());
            entry.setUpdatedDate(score.getCreatedAt());
            SyndEnclosureImpl enclosure = new SyndEnclosureImpl();
            enclosure.setUrl(score.game.cover);
            enclosure.setType(score.game.getCoverType());
            entry.setEnclosures(Lists.newArrayList(enclosure));
            entries.add(entry);
        }
        Writer writer = new StringWriter();
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, writer);
        writer.close();
        Controller.response().setContentType("application/rss+xml");
        return writer.toString();
    }
}
