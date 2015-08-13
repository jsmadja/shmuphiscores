package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.google.common.base.Function;
import models.Difficulty;
import models.Game;
import models.Mode;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.onecc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.collect.Collections2.transform;

public class OneCreditController extends Controller {

    public static Result index() {
        OneCreditPage oneCreditPage = new OneCreditPage();

        Collection<String> platforms = transform(Ebean.createSqlQuery("SELECT DISTINCT name FROM platform ORDER BY name").findList(), new Function<SqlRow, String>() {
            @Nullable
            @Override
            public String apply(@Nullable SqlRow sqlRow) {
                return sqlRow.getString("name");
            }
        });
        for (final String platform : platforms) {
            OneCreditPlatform oneCreditPlatform = oneCreditPage.addPlatform(platform);
            Collection<Game> games = PlatformController.getGamesByPlatform(platform);
            for (Game game : games) {
                Collection<Score> scores = Score.finder.
                        where().eq("onecc", true).eq("game", game).eq("platform.name", platform).
                        join("player").
                        fetch("player").
                        fetch("difficulty").
                        fetch("mode").
                        findList();
                if (!scores.isEmpty()) {
                    OneCreditGame oneCreditGame = new OneCreditGame(game);
                    for (Score score : scores) {
                        oneCreditGame.addPlayer(score.player, score.difficulty, score.mode);
                    }
                    oneCreditPlatform.addGame(oneCreditGame);
                }
            }
        }
        return ok(onecc.render(oneCreditPage));
    }

    public static class OneCreditPage {

        public Collection<OneCreditPlatform> platforms = new ArrayList<OneCreditPlatform>();

        public OneCreditPlatform addPlatform(String platform) {
            OneCreditPlatform oneCreditPlatform = new OneCreditPlatform(platform);
            this.platforms.add(oneCreditPlatform);
            return oneCreditPlatform;
        }
    }

    public static class OneCreditPlatform {
        public String platform;
        public Collection<OneCreditGame> games = new ArrayList<OneCreditGame>();

        public OneCreditPlatform(String platform) {
            this.platform = platform;
        }

        public void addGame(OneCreditGame oneCreditGame) {
            this.games.add(oneCreditGame);
        }
    }

    public static class OneCreditGame {
        public Game game;
        public Map<String, Category> categories = new TreeMap<String, Category>();

        public OneCreditGame(Game game) {
            this.game = game;
        }

        public void addPlayer(Player player, Difficulty difficulty, Mode mode) {
            String key = key(difficulty, mode);
            Category category = categories.get(key);
            if (category == null) {
                category = new Category(difficulty, mode);
                categories.put(key, category);
            }
            category.add(player);
        }

        private String key(Difficulty difficulty, Mode mode) {
            return game.title + "_" + (difficulty == null ? "" : difficulty.name) + "_" + (mode == null ? "" : mode.name);
        }
    }

    public static class Category {
        public Difficulty difficulty;
        public Mode mode;
        public Collection<Player> players = new ArrayList<Player>();

        public Category(Difficulty difficulty, Mode mode) {
            this.difficulty = difficulty;
            this.mode = mode;
        }

        public void add(Player player) {
            this.players.add(player);
        }

        public String key() {
            return String.format("%s%s", difficulty == null ? "" : difficulty.name, mode == null ? "" : mode.name);
        }
    }
}
