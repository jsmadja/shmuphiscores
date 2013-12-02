package models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScoreTest {

    private Game game = new Game("title", "cover", "thread");
    private Player player = new Player("player");
    private Stage stage = new Stage("Stage 1");
    private Mode mode = new Mode("mode");
    private Difficulty difficulty = new Difficulty("difficulty");
    private String comment = "comment";
    private Platform platform = new Platform("platform");
    private String photo = "photo";

    @Before
    public void init() {
        mode.id = 0L;
        difficulty.id = 0L;
        platform.id = 0L;
        player.id = 0L;
        stage.id = 0L;
    }

    @Test
    public void should_detect_that_score1_is_worse_than_score2() {
        Score score1 = new Score(game, player, stage, mode, difficulty, comment, platform, 1L, photo);
        Score score2 = new Score(game, player, stage, mode, difficulty, comment, platform, 2L, photo);

        player.scores.add(score2);

        assertTrue(score1.isWorstThanOlders());
    }

    @Test
    public void should_detect_that_score1_is_better_than_score2() {
        Score score1 = new Score(game, player, stage, mode, difficulty, comment, platform, 2L, photo);
        Score score2 = new Score(game, player, stage, mode, difficulty, comment, platform, 1L, photo);

        player.scores.add(score2);

        assertFalse(score1.isWorstThanOlders());
    }

    @Test
    public void should_detect_worse_only_for_same_player() {
        Player player2 = new Player("player2");
        player2.id = 2L;

        Score score1 = new Score(game, player, stage, mode, difficulty, comment, platform, 1L, photo);
        Score score2 = new Score(game, player2, stage, mode, difficulty, comment, platform, 2L, photo);

        game.scores.add(score2);
        player2.scores.add(score2);

        assertFalse(score1.isWorstThanOlders());
    }

    @Test
    public void value_is_important_in_comparison() {
        Score score1 = new Score(game, player, stage, mode, difficulty, comment, platform, 1L, photo);
        Score score2 = new Score(game, player, stage, mode, difficulty, comment, platform, 2L, photo);
        assertTrue(score1.compareTo(score2) > 0);
    }

    @Test
    public void stage_is_important_in_comparison() {
        Score score1 = new Score(game, player, stage, mode, difficulty, comment, platform, 1L, photo);
        stage.id = 1L;

        Stage stage2 = new Stage("Stage 2");
        stage2.id = 2L;
        Score score2 = new Score(game, player, stage2, mode, difficulty, comment, platform, 1L, photo);

        assertTrue(score1.compareTo(score2) > 0);
    }

    @Test
    public void mode_is_not_important_in_comparison() {
        stage.id = 1L;
        Mode mode1 = new Mode("mode1");
        mode1.id = 1L;

        Score score1 = new Score(game, player, stage, mode1, difficulty, comment, platform, 1L, photo);

        Mode mode2 = new Mode("mode2");
        mode2.id = 2L;

        Score score2 = new Score(game, player, stage, mode2, difficulty, comment, platform, 1L, photo);

        assertFalse(score1.isWorstThan(score2));
        assertFalse(score2.isWorstThan(score1));
    }

}
