package drawer;

import models.Game;
import models.Score;

import java.awt.*;
import java.util.List;

import static java.lang.Math.max;

public class RankingGameConfiguration {

    static final int fontHeight = 12;
    static final int width = 652;
    static final Color COLOR_SHMUP_TITLE = new Color(46, 13, 34);
    public static final Color COLOR_SHMUP_GREY = new Color(240, 243, 244);
    static final Color COLOR_SHMUP_TEXT = new Color(102, 102, 102);

    int maxStageLength;
    int maxPlatformLength;

    public RankingGameConfiguration(Game game) {
        List<Score> scores = game.scores;
        for (Score score : scores) {
            if (score.stage != null) {
                maxStageLength = max(score.stage.name.length(), maxStageLength);
            }
            if (score.platform != null) {
                maxPlatformLength = max(score.platform.name.length(), maxPlatformLength);
            }
        }
    }
}
