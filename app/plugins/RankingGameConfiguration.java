package plugins;

import models.Game;
import models.Score;

import java.util.List;

public class RankingGameConfiguration {

    public int maxStageLength;
    public int maxPlatformLength;

    public RankingGameConfiguration(Game game) {
        List<Score> scores = game.scores;
        for (Score score : scores) {
            maxStageLength = Math.max(score.stage.name.length(), maxStageLength);
            maxPlatformLength = Math.max(score.platform.name.length(), maxPlatformLength);
        }
    }
}
