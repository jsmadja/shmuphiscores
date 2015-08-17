package decorators;

import models.Score;
import org.apache.commons.lang3.StringUtils;

public class ScoreDecorator {
    private final Score score;

    public ScoreDecorator(Score score) {
        this.score = score;
    }

    public String replay() {
        if (score.replay != null && StringUtils.isNotBlank(score.replay)) {
            if (score.replay.contains("youtube")) {
                return "[youtube]" + score.replay.split("v=")[1] + "[/youtube]";
            } else {
                return "Replay : " + score.replay;
            }
        }
        return null;
    }

    private String rankSuffix() {
        if (score.rank == null) {
            return "";
        }
        if (score.rank == 1) {
            return "ère";
        }
        return "ème";
    }
}
