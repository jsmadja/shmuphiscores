package plugins;

import models.Difficulty;
import models.Mode;
import models.Score;
import models.Scores;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static plugins.RankingDrawer.*;

public class RankingPicture {

    public static BufferedImage createRankingPicture(models.Game game) {
        Collection<Difficulty> difficulties = new ArrayList<Difficulty>(game.difficulties);
        Collection<Mode> modes = new ArrayList<Mode>(game.modes);
        if (difficulties.isEmpty()) {
            difficulties.add(null);
        }
        if (modes.isEmpty()) {
            modes.add(null);
        }
        List<PictureLine> pictureLines = new ArrayList<PictureLine>();
        for (Mode mode : modes) {
            for (Difficulty difficulty : difficulties) {
                List<Score> scores = new ArrayList<Score>(game.scores(difficulty, mode));
                Collections.sort(scores);
                scores = Scores.keepBestScoresForEachPlayer(scores);
                if (!scores.isEmpty()) {
                    pictureLines.add(new BreakLine());
                    pictureLines.add(new GameLine(game, mode, difficulty));
                    pictureLines.add(new BreakLine());
                    for (Score score : scores) {
                        pictureLines.add(new ScoreLine(score));
                    }
                    pictureLines.add(new BreakLine());
                }
            }
        }
        return computeRanking(pictureLines);
    }

}
