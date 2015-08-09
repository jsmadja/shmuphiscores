package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.google.common.base.Function;
import models.Game;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.onecc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.collect.Collections2.transform;

public class OneCreditController extends Controller {

    public static Result index() {
        List<Score> all = Score.finder.all();
        for (Score score : all) {
            if(!score.onecc && score.is1CC()) {
                score.onecc = true;
                score.update();
            }
        }
        OneCreditPage oneCreditPage = new OneCreditPage();

        Collection<String> platforms = transform(Ebean.createSqlQuery("SELECT DISTINCT name FROM Platform ORDER BY name").findList(), new Function<SqlRow, String>() {
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
                List<Score> oneccs = game.oneccs;
                if (!oneccs.isEmpty()) {
                    OneCreditGame oneCreditGame = oneCreditPlatform.addGame(game);
                    Set<Player> players = new TreeSet<Player>(transform(oneccs, new Function<Score, Player>() {
                        @Nullable
                        @Override
                        public Player apply(Score score) {
                            return score.player;
                        }
                    }));
                    for (Player player : players) {
                        oneCreditGame.addPlayer(player);
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

        public OneCreditGame addGame(Game game) {
            OneCreditGame oneCreditGame = new OneCreditGame(game);
            this.games.add(oneCreditGame);
            return oneCreditGame;
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
