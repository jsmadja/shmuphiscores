package models;

import org.fest.assertions.Assertions;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ScoreTest {

    Game game = new Game("TheGame", "The Cover", "The Thread");
    Player player = new Player("The Player");
    Stage stage = new Stage("TheStage");
    Mode mode = new Mode("TheMode");
    Difficulty difficulty = new Difficulty("TheDifficulty");
    Platform platform = new Platform("ThePlatform");

    @Test
    public void should_create_tweet() {
        Score score = new Score(game, player, stage, mode, difficulty, "comment", platform, 1245L, "http://www.photo.com", "http://www.replay.com");
        assertThat(score.tweet()).isEqualTo("TheGame - 1.245 pts - TheMode TheDifficulty - The Player - 0th");
    }

    @Test
    public void should_create_tweet_with_twitter_account() {
        player.twitter = "@player";
        Score score = new Score(game, player, stage, mode, difficulty, "comment", platform, 1245L, "http://www.photo.com", "http://www.replay.com");
        assertThat(score.tweet()).isEqualTo("TheGame - 1.245 pts - TheMode TheDifficulty - The Player (@player) - 0th");
    }

}
