package drawer;

import models.Player;
import models.Score;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.avaje.ebean.Ebean.find;
import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
import static drawer.RankingGameConfiguration.COLOR_SHMUP_GREY;
import static java.awt.Font.PLAIN;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.apache.commons.lang3.StringUtils.leftPad;

public class MedalsPicture {

    public static final int WIDTH = 145;
    public static final float HEIGHT = 30;
    private final static Font gameFont = new Font("Lucida", PLAIN, 11);

    public static BufferedImage createMedalsPicture(Player player, Color color) {
        BufferedImage bi = new BufferedImage(WIDTH, (int) HEIGHT, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fontMetrics = graphics.getFontMetrics();

        graphics.setColor(color);
        graphics.fillRect(0, 0, WIDTH, (int) HEIGHT);
        graphics.setFont(gameFont);

        Integer firstRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 1))).findRowCount();
        draw(graphics, "✯", firstRankCount, 0, fontMetrics, new Color(255, 215, 0));
        int secondRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 2))).findRowCount();
        draw(graphics, "✯", secondRankCount, 35, fontMetrics, new Color(192, 192, 192));
        int thirdRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 3))).findRowCount();
        draw(graphics, "✯", thirdRankCount, 70, fontMetrics, new Color(205, 127, 50));
        int oneCreditCount = player.computeOneCredit();
        draw(graphics, "✯", oneCreditCount, 105, fontMetrics, Color.BLACK);
        return bi;
    }

    private static void draw(Graphics2D graphics, String picto, Integer count, int i, FontMetrics fontMetrics, Color color) {
        graphics.setColor(color);
        graphics.setFont(new Font("Lucida", PLAIN, 30));
        graphics.drawString(picto + " ", i, HEIGHT - HEIGHT / 4);
        graphics.setFont(gameFont);
        graphics.drawString(pad(count), i + 22, HEIGHT - HEIGHT / 4);
    }

    private static String pad(Integer value) {
        return leftPad(value.toString(), 2);
    }

    public static BufferedImage createBlankImage() {
        return new BufferedImage(1, 1, TYPE_INT_ARGB);
    }
}
