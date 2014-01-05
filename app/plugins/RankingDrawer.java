package plugins;

import com.google.common.base.Strings;
import models.Difficulty;
import models.Mode;
import models.Score;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static java.awt.Color.*;
import static java.awt.Font.*;
import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;

public class RankingDrawer {

    public static final Color COLOR_SHMUP_GREY = new Color(240, 243, 244);
    public static final Color COLOR_SHMUP_TITLE = new Color(46, 13, 34);
    public static final Color COLOR_SHMUP_PLAYER = new Color(247, 127, 74);
    public static final Color COLOR_SHMUP_SCORE = new Color(245, 70, 52);
    public static final Color COLOR_SHMUP_COMMENT = new Color(193, 193, 193);

    interface PictureLine {
        void draw(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration);
    }

    private static int width = 652;

    private static final int fontHeight = 12;
    private static final int fontWidth = 12;

    private final static Font normalFont = new Font("Liberation Mono", PLAIN, 12);
    private final static Font parameterFont = new Font("Liberation Mono", PLAIN, 14);
    private final static Font gameFont = new Font("Liberation Mono", BOLD | ITALIC, 16);
    private final static Font playerFont = new Font("Liberation Mono", BOLD, 12);
    private final static Font scoreFont = new Font("Liberation Mono", BOLD, 14);
    private final static Font stageFont = new Font("Liberation Mono", ITALIC, 14);

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
        public void draw(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
            graphics.setColor(COLOR_SHMUP_TITLE);
            graphics.setFont(gameFont);
            String title = "";

            if (mode != null) {
                title = mode.toString();
                if (difficulty != null) {
                    title += " : " + difficulty.toString();
                }
            } else {
                if (difficulty != null) {
                    title = difficulty.toString();
                }
            }
            graphics.drawString(title, 30, y);
        }
    }

    public static class ScoreLine implements PictureLine {

        private final models.Score score;

        public ScoreLine(Score score) {
            this.score = score;
        }

        @Override
        public void draw(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
            pyjama(graphics, y);
            rank(graphics, y);
            player(graphics, y);
            score(graphics, y);
            stage(graphics, y, rankingGameConfiguration);
            platformAndComment(graphics, y, rankingGameConfiguration);
        }

        private void platformAndComment(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
            String platform = score.platform == null ? "" : leftPad(score.platform.name, rankingGameConfiguration.maxPlatformLength);
            if (score.game.platforms.isEmpty() || score.game.platforms.size() == 1) {
                platform = "";
            }
            if (score.rank() == 1) {
                graphics.setColor(WHITE);
            } else {
                graphics.setColor(COLOR_SHMUP_COMMENT);
            }
            graphics.setFont(parameterFont);
            graphics.drawString(platform + "    " + defaultString(score.comment).trim(), STAGE_PLATFORM_X + (rankingGameConfiguration.maxStageLength * fontWidth), y);
        }

        private void stage(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
            if (score.rank() == 1) {
                graphics.setColor(WHITE);
            } else {
                graphics.setColor(DARK_GRAY);
            }
            graphics.setFont(parameterFont);
            String stage = score.stage == null ? "" : rightPad(score.stage.toString(), rankingGameConfiguration.maxStageLength);
            graphics.setFont(stageFont);
            graphics.drawString(stage + "   ", STAGE_PLATFORM_X, y);
        }

        private void score(Graphics2D graphics, int y) {
            if (score.rank() == 1) {
                graphics.setColor(WHITE);
            } else {
                graphics.setColor(COLOR_SHMUP_SCORE);
            }
            graphics.setFont(scoreFont);
            graphics.drawString(Strings.padStart(score.formattedValue(), Long.valueOf(Long.MAX_VALUE).toString().length() + 6, ' '), SCORE_X, y);
        }

        private void player(Graphics2D graphics, int y) {
            graphics.setFont(playerFont);

            if (score.rank() == 1) {
                graphics.setColor(WHITE);
            } else {
                graphics.setColor(COLOR_SHMUP_PLAYER);
            }
            graphics.drawString(score.player.name, 30, y);
        }

        private void rank(Graphics2D graphics, int y) {
            if (score.rank() == 1) {
                graphics.setColor(WHITE);
            } else {
                graphics.setColor(GRAY);
            }
            graphics.setFont(normalFont);
            String rank = leftPad(score.rank() + ".", 3);
            graphics.drawString(rank, 0, y);
        }

        private void pyjama(Graphics2D graphics, int y) {
            if (score.rank() == 1) {
                graphics.setColor(COLOR_SHMUP_TITLE);
            } else {
                if (score.rank() % 2 == 1) {
                    graphics.setColor(WHITE);
                } else {
                    graphics.setColor(COLOR_SHMUP_GREY);
                }
            }
            graphics.fillRect(0, y - fontHeight, width, y);
        }
    }

    public static class BreakLine implements PictureLine {

        @Override
        public void draw(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
            graphics.setColor(COLOR_SHMUP_GREY);
            graphics.fillRect(0, y - fontHeight, width, y);
            graphics.drawString("", 0, y);
        }
    }

    public static BufferedImage computeRanking(List<PictureLine> pictureLines, RankingGameConfiguration rankingGameConfiguration) {
        int height = (fontHeight+5) * (pictureLines.size());
        if (height == 0) {
            height = 1;
        }
        BufferedImage bi = new BufferedImage(width, height, TYPE_INT_ARGB);
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
