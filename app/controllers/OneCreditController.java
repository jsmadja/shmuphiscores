package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import models.Game;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.onecc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.collect.Collections2.filter;
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
        for (String platform : platforms) {
            OneCreditPlatform oneCreditPlatform = oneCreditPage.addPlatform(platform);
            Collection<Game> games = PlatformController.getGamesByPlatform(platform);
            for (Game game : games) {
                if (!game.oneccs.isEmpty()) {
                    OneCreditGame oneCreditGame = new OneCreditGame(game);
                    Collection<Score> scores = filter(game.oneccs, new Predicate<Score>() {
                        @Override
                        public boolean apply(@Nullable Score score) {
                            return score.platform == null || score.platform.name.equalsIgnoreCase(platform);
                        }
                    });
                    if (!scores.isEmpty()) {
                        for (Score score : scores) {
                            oneCreditGame.addPlayer(score.player);
                        }
                        oneCreditPlatform.addGame(oneCreditGame);
                    }
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
        public Collection<Player> players = new ArrayList<Player>();

        public OneCreditGame(Game game) {
            this.game = game;
        }

        public void addPlayer(Player player) {
            this.players.add(player);
        }
    }
}
