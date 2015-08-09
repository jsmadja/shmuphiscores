package decorators;

import models.Score;
import org.apache.commons.lang3.StringUtils;

public class ScoreDecorator {
    private final Score score;

    public ScoreDecorator(Score score) {
        this.score = score;
    }

    public String format() {
        String str = "[color=#800000]J'ai atteint le [b]stage " + score.stage.name + "[/b] de " + score.game.title + " sur [b]" + score.platform.name + "[/b] avec un score de [size=110][b]" + score.formattedValue() + " points[/b][/size], ce qui me classe en [b]" + score.rank() + rankSuffix() + "[/b] position du ranking";

        if (score.mode != null) {
            str += " en mode [b]" + score.mode.name + "[/b]";
            if (score.difficulty != null) {
                str += " et";
            }
        }
        if (score.difficulty != null) {
            str += " en difficulté [b]" + score.difficulty.name + "[/b]";
        }
        str += ".";
        str += "[/color]";
        return str;
    }

    public String comment() {
        if (StringUtils.isNotEmpty(score.comment)) {
            return "Commentaire : [i]\"" + score.comment + "\"[/i]";
        }
        return null;
    }

    public String replay() {
        if (score.replay != null) {
            if (score.replay.contains("youtube")) {
                return "[youtube]" + score.replay.split("v=")[1] + "[/youtube]";
            } else {
                return "Replay : " + score.replay;
            }
        }
        return null;
    }

    private String rankSuffix() {
        if (score.rank() == 1) {
            return "ère";
        }
        return "ème";
    }
}