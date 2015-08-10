package decorators;

import models.Difficulty;
import models.Game;
import models.Mode;
import models.Platform;
import models.Player;
import models.Score;
import models.Ship;
import models.Stage;
import org.junit.Test;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;

public class ScoreDecoratorTest {

    @Test
    public void should_full_format_Score() {
        Game game = new Game("Ketsui", "cover", "thread");
        Player player = new Player("me");
        Stage stage = new Stage("2-4");
        Ship ship = new Ship("ship");
        Mode mode = new Mode("Arcade");
        Difficulty difficulty = new Difficulty("Normal");
        String comment = "no miss sur le premier loop";
        Platform platform = new Platform("XBox 360");
        BigDecimal value = BigDecimal.valueOf(360000000L);
        String photo = "http://volaju.dscloud.me/games/scoring/ketsui/arcade.png";
        String replay = "https://www.youtube.com/watch?v=Uq_ylvWqm3U";
        Score score = new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        score.setRank(67);

        ScoreDecorator scoreDecorator = new ScoreDecorator(score);
        assertThat(scoreDecorator.format()).isEqualTo(
                "[color=#800000]J'ai atteint le [b]stage 2-4[/b] de Ketsui sur [b]XBox 360[/b] avec un score de [size=110][b]360.000.000 points[/b][/size], ce qui me classe en [b]67ème[/b] position du ranking en mode [b]Arcade[/b] et en difficulté [b]Normal[/b].[/color]");
    }

    @Test
    public void should_full_format_Score_no_comment() {
        Game game = new Game("Ketsui", "cover", "thread");
        Player player = new Player("me");
        Stage stage = new Stage("2-4");
        Ship ship = new Ship("ship");
        Mode mode = new Mode("Arcade");
        Difficulty difficulty = new Difficulty("Normal");
        String comment = null;
        Platform platform = new Platform("XBox 360");
        BigDecimal value = BigDecimal.valueOf(360000000L);
        String photo = "http://volaju.dscloud.me/games/scoring/ketsui/arcade.png";
        String replay = null;
        Score score = new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        score.setRank(67);

        ScoreDecorator scoreDecorator = new ScoreDecorator(score);
        assertThat(scoreDecorator.format()).isEqualTo(
                "[color=#800000]J'ai atteint le [b]stage 2-4[/b] de Ketsui sur [b]XBox 360[/b] avec un score de [size=110][b]360.000.000 points[/b][/size], ce qui me classe en [b]67ème[/b] position du ranking en mode [b]Arcade[/b] et en difficulté [b]Normal[/b].[/color]");
    }

    @Test
    public void should_full_format_Score_no_mode() {
        Game game = new Game("Ketsui", "cover", "thread");
        Player player = new Player("me");
        Stage stage = new Stage("2-4");
        Ship ship = new Ship("ship");
        Mode mode = null;
        Difficulty difficulty = new Difficulty("Normal");
        String comment = null;
        Platform platform = new Platform("XBox 360");
        BigDecimal value = BigDecimal.valueOf(360000000L);
        String photo = "http://volaju.dscloud.me/games/scoring/ketsui/arcade.png";
        String replay = null;
        Score score = new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        score.setRank(67);

        ScoreDecorator scoreDecorator = new ScoreDecorator(score);
        assertThat(scoreDecorator.format()).isEqualTo(
                "[color=#800000]J'ai atteint le [b]stage 2-4[/b] de Ketsui sur [b]XBox 360[/b] avec un score de [size=110][b]360.000.000 points[/b][/size], ce qui me classe en [b]67ème[/b] position du ranking en difficulté [b]Normal[/b].[/color]");
    }

    @Test
    public void should_full_format_Score_no_difficulty() {
        Game game = new Game("Ketsui", "cover", "thread");
        Player player = new Player("me");
        Stage stage = new Stage("2-4");
        Ship ship = new Ship("ship");
        Mode mode = null;
        Difficulty difficulty = null;
        String comment = null;
        Platform platform = new Platform("XBox 360");
        BigDecimal value = BigDecimal.valueOf(360000000L);
        String photo = "http://volaju.dscloud.me/games/scoring/ketsui/arcade.png";
        String replay = null;
        Score score = new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        score.setRank(67);

        ScoreDecorator scoreDecorator = new ScoreDecorator(score);
        assertThat(scoreDecorator.format()).isEqualTo(
                "[color=#800000]J'ai atteint le [b]stage 2-4[/b] de Ketsui sur [b]XBox 360[/b] avec un score de [size=110][b]360.000.000 points[/b][/size], ce qui me classe en [b]67ème[/b] position du ranking.[/color]");
    }

    @Test
    public void should_full_format_Score_no_difficulty_but_with_mode() {
        Game game = new Game("Ketsui", "cover", "thread");
        Player player = new Player("me");
        Stage stage = new Stage("2-4");
        Ship ship = new Ship("ship");
        Mode mode = new Mode("Arcade");
        Difficulty difficulty = null;
        String comment = null;
        Platform platform = new Platform("XBox 360");
        BigDecimal value = BigDecimal.valueOf(360000000L);
        String photo = "http://volaju.dscloud.me/games/scoring/ketsui/arcade.png";
        String replay = null;
        Score score = new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        score.setRank(67);

        ScoreDecorator scoreDecorator = new ScoreDecorator(score);
        assertThat(scoreDecorator.format()).isEqualTo(
                "[color=#800000]J'ai atteint le [b]stage 2-4[/b] de Ketsui sur [b]XBox 360[/b] avec un score de [size=110][b]360.000.000 points[/b][/size], ce qui me classe en [b]67ème[/b] position du ranking en mode [b]Arcade[/b].[/color]");
    }

    @Test
    public void should_full_format_Score_first_place() {
        Game game = new Game("Ketsui", "cover", "thread");
        Player player = new Player("me");
        Stage stage = new Stage("2-4");
        Ship ship = new Ship("ship");
        Mode mode = null;
        Difficulty difficulty = null;
        String comment = null;
        Platform platform = new Platform("XBox 360");
        BigDecimal value = BigDecimal.valueOf(360000000L);
        String photo = "http://volaju.dscloud.me/games/scoring/ketsui/arcade.png";
        String replay = null;
        Score score = new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        score.setRank(1);

        ScoreDecorator scoreDecorator = new ScoreDecorator(score);
        assertThat(scoreDecorator.format()).isEqualTo(
                "[color=#800000]J'ai atteint le [b]stage 2-4[/b] de Ketsui sur [b]XBox 360[/b] avec un score de [size=110][b]360.000.000 points[/b][/size], ce qui me classe en [b]1ère[/b] position du ranking.[/color]");
    }

    @Test
    public void should_full_format_Score_with_dailymotion() {
        Game game = new Game("Ketsui", "cover", "thread");
        Player player = new Player("me");
        Stage stage = new Stage("2-4");
        Ship ship = new Ship("ship");
        Mode mode = null;
        Difficulty difficulty = null;
        String comment = null;
        Platform platform = new Platform("XBox 360");
        BigDecimal value = BigDecimal.valueOf(360000000L);
        String photo = "http://volaju.dscloud.me/games/scoring/ketsui/arcade.png";
        String replay = "http://www.dailymotion.com/video/x2z40n8_x-files-deuxieme-bande-annonce_shortfilms";
        Score score = new Score(game, player, stage, ship, mode, difficulty, comment, platform, value, photo, replay);
        score.setRank(1);

        ScoreDecorator scoreDecorator = new ScoreDecorator(score);
        assertThat(scoreDecorator.format()).isEqualTo(
                "[color=#800000]J'ai atteint le [b]stage 2-4[/b] de Ketsui sur [b]XBox 360[/b] avec un score de [size=110][b]360.000.000 points[/b][/size], ce qui me classe en [b]1ère[/b] position du ranking.[/color]");
    }

}