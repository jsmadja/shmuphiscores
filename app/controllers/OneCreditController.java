package controllers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import models.Game;
import models.Platform;
import models.Player;
import models.Score;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.onecc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

public class OneCreditController extends Controller {

    public static Result index() {

        OneCreditPage oneCreditPage = new OneCreditPage();

        TreeSet<String> platforms = new TreeSet<String>(transform(Platform.finder.all(), new Function<Platform, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Platform platform) {
                return platform.name;
            }
        }));
        for (String platform : platforms) {
            OneCreditPlatform oneCreditPlatform = oneCreditPage.addPlatform(platform.toUpperCase());
            Collection<Game> games = PlatformController.getGamesByPlatform(platform);
            for (Game game : games) {
                game.scores = new ArrayList<Score>(filter(game.scores, new Predicate<Score>() {
                    @Override
                    public boolean apply(Score score) {
                        return score.is1CC();
                    }
                }));
                if (!game.scores.isEmpty()) {
                    OneCreditGame oneCreditGame = oneCreditPlatform.addGame(game);
                    TreeSet<Player> players = new TreeSet<Player>(transform(game.scores, new Function<Score, Player>() {
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
