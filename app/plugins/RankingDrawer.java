package plugins;

import models.*;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static java.awt.Color.BLACK;
import static java.awt.Color.ORANGE;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class RankingDrawer {

    interface PictureLine {
        void draw(Graphics2D graphics, int y);
    }

    private final static Font normalFont = new Font("Verdana", Font.PLAIN, 12);
    private final static Font playerFont = new Font("Verdana", Font.BOLD, 12);
    private final static Font scoreFont = new Font("Courier", Font.BOLD, 16);

    public static class GameLine implements PictureLine {

        private final Game game;
        private final Platform platform;
        private final Mode mode;
        private final Difficulty difficulty;

        public GameLine(models.Game game, Platform platform, Mode mode, Difficulty difficulty) {
            this.game = game;
            this.platform = platform;
            this.mode = mode;
            this.difficulty = difficulty;
        }

        @Override
        public void draw(Graphics2D graphics, int y) {
            graphics.setPaint(BLACK);
            graphics.setFont(normalFont);
            graphics.drawString(game.title + " " + platform + " " + (mode == null ? "" : mode) + " " + (difficulty == null ? "" : difficulty), 0, y);
        }
    }

    public static class ScoreLine implements PictureLine {

        private final models.Score score;

        public ScoreLine(Score score) {
            this.score = score;
        }

        @Override
        public void draw(Graphics2D graphics, int y) {
            graphics.setPaint(BLACK);
            graphics.setFont(normalFont);
            String rank = score.rank() + ".";
            String player = score.player.name;
            graphics.drawString(rank, 0, y);

            graphics.setFont(playerFont);
            graphics.drawString(player, 30, y);

            graphics.setColor(ORANGE);
            graphics.setFont(scoreFont);
            graphics.drawString(StringUtils.leftPad(score.formattedValue(), 13, " "), 200, y);

            graphics.setFont(normalFont);
            graphics.setColor(BLACK);
            graphics.drawString(score.stage == null ? "" : score.stage.toString(), 380, y);
        }
    }

    public static class BreakLine implements PictureLine {

        @Override
        public void draw(Graphics2D graphics, int y) {
            graphics.drawString("", 0, y);
        }
    }

    public static BufferedImage computeRanking(List<PictureLine> pictureLines) {
        int fontHeight = 13;
        int width = 652;
        int height = fontHeight * pictureLines.size();
        if (height == 0) {
            height = 1;
        }
        BufferedImage bi = new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
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
