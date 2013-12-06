package plugins;

import models.Game;
import models.Score;

import java.util.List;

import static java.lang.Math.max;

public class RankingGameConfiguration {

    public int maxStageLength;
    public int maxPlatformLength;

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
