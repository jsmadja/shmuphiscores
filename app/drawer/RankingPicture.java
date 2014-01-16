package drawer;

import models.Difficulty;
import models.Mode;
import models.Ranking;
import models.Score;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

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
        for (Ranking ranking : game.rankings()) {
            if (!ranking.scores.isEmpty()) {
                pictureLines.add(new BreakLine());
                pictureLines.add(new GameLine(ranking));
                pictureLines.add(new BreakLine());
                for (Score score : ranking.scores) {
                    pictureLines.add(new ScoreLine(score));
                }
                pictureLines.add(new BreakLine());
            }
        }
        return computeRanking(pictureLines, new RankingGameConfiguration(game));
    }

    private static BufferedImage computeRanking(List<PictureLine> pictureLines, RankingGameConfiguration rankingGameConfiguration) {
        int height = (RankingGameConfiguration.fontHeight + 5) * (pictureLines.size());
        if (height == 0) {
            height = 1;
        }
        BufferedImage bi = new BufferedImage(RankingGameConfiguration.width, height, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int stringHeight = fontMetrics.getAscent() + 5;
        for (int i = 0; i < pictureLines.size(); i++) {
            pictureLines.get(i).draw(graphics, 10 + (i * stringHeight), rankingGameConfiguration);
        }
        return bi;
    }

}
