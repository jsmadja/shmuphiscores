package plugins;

import com.google.common.base.Strings;
import models.Difficulty;
import models.Mode;
import models.Score;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static java.awt.Color.*;
import static java.awt.Font.BOLD;
import static java.awt.Font.PLAIN;
import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;

public class RankingDrawer {

    interface PictureLine {
        void draw(Graphics2D graphics, int y);
    }

    private static final int fontHeight = 12;

    private final static Font normalFont = new Font("Courier", PLAIN, 14);
    private final static Font parameterFont = new Font("Courier", BOLD, 14);
    private final static Font gameFont = new Font("Verdana", BOLD, 12);
    private final static Font playerFont = new Font("Courier", BOLD, 12);
    private final static Font scoreFont = new Font("Courier", BOLD, 14);

    private static int SCORE_X = 80;
    private static int STAGE_PLATFORM_X = SCORE_X + 220;

    public static class GameLine implements PictureLine {

        private final Mode mode;
        private final Difficulty difficulty;

        public GameLine(Mode mode, Difficulty difficulty) {
            this.mode = mode;
            this.difficulty = difficulty;
        }

        @Override
        public void draw(Graphics2D graphics, int y) {
            graphics.setColor(RED);
            graphics.setFont(gameFont);
            graphics.drawString((mode == null ? "" : mode) + " " + (difficulty == null ? "" : difficulty), 30, y);
        }
    }

    public static class ScoreLine implements PictureLine {

        private final models.Score score;

        public ScoreLine(Score score) {
            this.score = score;
        }

        @Override
        public void draw(Graphics2D graphics, int y) {
            graphics.setColor(DARK_GRAY);
            graphics.setFont(normalFont);
            String rank = leftPad(score.rank() + ".", 3);
            String player = score.player.name;
            graphics.drawString(rank, 0, y);

            graphics.setFont(playerFont);
            graphics.drawString(player, 30, y);

            graphics.setColor(BLUE);
            graphics.setFont(scoreFont);
            graphics.drawString(Strings.padStart(score.formattedValue(), Long.valueOf(Long.MAX_VALUE).toString().length() + 6, ' '), SCORE_X, y);


            graphics.setColor(DARK_GRAY);
            graphics.setFont(parameterFont);
            String platform = score.platform == null ? "" : leftPad(score.platform.name, 10);
            if (score.game.platforms.isEmpty() || score.game.platforms.size() == 1) {
                platform = "";
            }
            String stage = score.stage == null ? "" : rightPad(score.stage.toString(), 20);
            graphics.drawString(stage + " " + platform+ " "+score.comment, STAGE_PLATFORM_X, y);
        }
    }

    public static class BreakLine implements PictureLine {

        @Override
        public void draw(Graphics2D graphics, int y) {
            graphics.drawString("", 0, y);
        }
    }

    public static BufferedImage computeRanking(List<PictureLine> pictureLines) {
        int width = 710;
        int height = fontHeight * pictureLines.size();
        if (height == 0) {
            height = 1;
        }
        BufferedImage bi = new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();

        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);

        graphics.setColor(new Color(240, 243, 244));
        graphics.fillRect(0, 0, width, height);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int stringHeight = fontMetrics.getAscent();
        for (int i = 0; i < pictureLines.size(); i++) {
            pictureLines.get(i).draw(graphics, 10 + (i * stringHeight));
        }
        return bi;
    }
}
