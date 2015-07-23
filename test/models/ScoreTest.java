package models;

import org.junit.Test;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;

public class ScoreTest {

    Game game = new Game("TheGame", "The Cover", "The Thread");
    Player player = new Player("The Player");
    Stage stage = new Stage("TheStage");
    Mode mode = new Mode("TheMode");
    Difficulty difficulty = new Difficulty("TheDifficulty");
    Platform platform = new Platform("ThePlatform");
    Ship ship = new Ship("Ship A");

    @Test
    public void should_create_tweet() {
        game.id = 1L;

        Score score = new Score(game, player, stage, ship, mode, difficulty, "comment", platform, BigDecimal.valueOf(1245L), "http://www.photo.com", "http://www.replay.com");
        assertThat(score.tweet()).isEqualTo("TheGame - 1.245 pts - TheMode TheDifficulty - The Player - 0th hiscores.shmup.com/game/1/TheGame");
    }

    @Test
    public void should_create_tweet_with_twitter_account() {
        game.id = 1L;
        player.twitter = "@player";
        Score score = new Score(game, player, stage, ship, mode, difficulty, "comment", platform, BigDecimal.valueOf(1245L), "http://www.photo.com", "http://www.replay.com");
        assertThat(score.tweet()).isEqualTo("TheGame - 1.245 pts - TheMode TheDifficulty - The Player (@player) - 0th hiscores.shmup.com/game/1/TheGame");
    }

}
