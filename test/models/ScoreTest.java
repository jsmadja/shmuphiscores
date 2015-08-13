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

    @Test
    public void worst_standard_value() {
        Game game = new Game("game", "", "");
        game.id = 1L;

        Score scoreA = new Score(BigDecimal.ONE);
        scoreA.game = game;

        Score scoreB = new Score(BigDecimal.TEN);
        scoreB.game = game;
        assertThat(scoreA.isWorstThan(scoreB)).isTrue();
    }

    @Test
    public void worst_standard_value_with_numeric_mode() {
        Game game = new Game("game", "", "");
        game.id = 1L;

        Mode mode = new Mode("mode");
        mode.id = 1L;

        Score scoreA = new Score(BigDecimal.ONE);
        scoreA.game = game;
        scoreA.mode = mode;

        Score scoreB = new Score(BigDecimal.TEN);
        scoreB.game = game;
        scoreB.mode = mode;

        assertThat(scoreA.isWorstThan(scoreB)).isTrue();
    }

    @Test
    public void worst_standard_value_with_difficulty() {
        Game game = new Game("game", "", "");
        game.id = 1L;

        Difficulty difficulty = new Difficulty("difficulty");
        difficulty.id = 1L;

        Score scoreA = new Score(BigDecimal.ONE);
        scoreA.game = game;
        scoreA.difficulty = difficulty;

        Score scoreB = new Score(BigDecimal.TEN);
        scoreB.game = game;
        scoreB.difficulty = difficulty;

        assertThat(scoreA.isWorstThan(scoreB)).isTrue();
    }

    @Test
    public void worst_standard_value_with_difficulty_and_mode() {
        Game game = new Game("game", "", "");
        game.id = 1L;

        Difficulty difficulty = new Difficulty("difficulty");
        difficulty.id = 1L;

        Mode mode = new Mode("mode");
        mode.id = 1L;

        Score scoreA = new Score(BigDecimal.ONE);
        scoreA.game = game;
        scoreA.difficulty = difficulty;
        scoreA.mode = mode;

        Score scoreB = new Score(BigDecimal.TEN);
        scoreB.game = game;
        scoreB.difficulty = difficulty;
        scoreB.mode = mode;

        assertThat(scoreA.isWorstThan(scoreB)).isTrue();
    }

    @Test
    public void worst_timer_value() {
        Game game = new Game("game", "", "");
        game.id = 1L;

        Mode mode = new Mode("mode");
        mode.scoreType = "timer";
        mode.id = 1L;

        Score scoreA = new Score(BigDecimal.ONE);
        scoreA.game = game;
        scoreA.mode = mode;

        Score scoreB = new Score(BigDecimal.TEN);
        scoreB.game = game;
        scoreB.mode = mode;
        assertThat(scoreA.isWorstThan(scoreB)).isFalse();
    }


}
