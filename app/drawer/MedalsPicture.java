package drawer;

import models.Player;
import models.Score;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.avaje.ebean.Ebean.find;
import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
import static drawer.RankingGameConfiguration.COLOR_SHMUP_GREY;
import static drawer.RankingGameConfiguration.COLOR_SHMUP_TITLE;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.apache.commons.lang3.StringUtils.leftPad;

public class MedalsPicture {

    public static final int WIDTH = 150;
    private final static Font gameFont = new Font("Liberation Mono", Font.PLAIN, 11);

    public static BufferedImage createMedalsPicture(Player player) {
        BufferedImage bi = new BufferedImage(WIDTH, 30, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fontMetrics = graphics.getFontMetrics();

        graphics.setColor(COLOR_SHMUP_GREY);
        graphics.fillRect(0, 0, WIDTH, fontMetrics.getAscent() + 5);
        graphics.setFont(gameFont);

        Integer firstRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 1))).findRowCount();
        draw(graphics, firstRankCount, 5, fontMetrics, Color.YELLOW);
        int secondRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 2))).findRowCount();
        draw(graphics, secondRankCount, 40, fontMetrics, Color.GRAY);
        int thirdRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 3))).findRowCount();
        draw(graphics, thirdRankCount, 75, fontMetrics, Color.ORANGE);
        int oneCreditCount = player.computeOneCredit();
        draw(graphics, oneCreditCount, 110, fontMetrics, Color.GREEN);
        return bi;
    }

    private static void draw(Graphics2D graphics, Integer firstRankCount, int i, FontMetrics fontMetrics, Color color) {
        graphics.setColor(color);
        graphics.setFont(new Font("Liberation Mono", Font.PLAIN, 36));
        graphics.drawString("• ", i, (float) fontMetrics.getAscent() * 1.5F);
        graphics.setFont(gameFont);
        graphics.setColor(COLOR_SHMUP_TITLE);
        graphics.drawString(pad(firstRankCount), i + 20, fontMetrics.getAscent());
    }

    private static String pad(Integer value) {
        return leftPad(value.toString(), 2);
    }
}
