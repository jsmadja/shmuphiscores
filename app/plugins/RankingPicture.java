package plugins;

import models.Difficulty;
import models.Mode;
import models.Ranking;
import models.Score;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
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
        for (Ranking ranking : game.rankings) {
            pictureLines.add(new BreakLine());
            pictureLines.add(new GameLine(ranking.mode, ranking.difficulty));
            pictureLines.add(new BreakLine());
            for (Score score : ranking.scores) {
                pictureLines.add(new ScoreLine(score));
            }
            pictureLines.add(new BreakLine());
        }
        return computeRanking(pictureLines, new RankingGameConfiguration(game));
    }

}
