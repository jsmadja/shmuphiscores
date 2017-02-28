package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.avaje.ebean.Expr.isNotNull;

public class Timeline {

    public Collection<Score> scores;

    public Timeline() {
        this.scores = Score.finder.
                where(isNotNull("rank")).
                orderBy("createdAt desc").
                setMaxRows(10).
                fetch("game", "*").
                fetch("stage", "*").
                fetch("ship", "*").
                fetch("platform", "*").
                fetch("player", "*").
                fetch("mode", "*").
                fetch("difficulty", "*").
                findList();
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

    public JsonNode json() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Score score : scores) {
            array.add(score.json());
        }
        return array;
    }

    private ObjectNode createPlayerNode(Score score) {
        ObjectNode playerNode = new ObjectNode(JsonNodeFactory.instance);
        playerNode.set("id", new LongNode(score.player.id));
        playerNode.set("name", new TextNode(score.player.name));
        return playerNode;
    }
}
