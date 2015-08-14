package drawer;

import models.Player;
import models.Score;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.avaje.ebean.Ebean.find;
import static com.avaje.ebean.Expr.and;
import static com.avaje.ebean.Expr.eq;
import static drawer.RankingGameConfiguration.COLOR_SHMUP_TEXT;
import static java.awt.Font.PLAIN;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.apache.commons.lang3.StringUtils.leftPad;

public class MedalsPicture {

    public static final int WIDTH = 145;
    public static final float HEIGHT = 30;
    private final static Font gameFont = new Font("Lucida", PLAIN, 11);

    public static BufferedImage createMedalsPicture(Player player, Color color) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File("/public/images/medailles.png"));
            Graphics2D graphics = bi.createGraphics();
            int space = 3;
            Integer firstRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 1))).findRowCount();
            draw(graphics, firstRankCount, 25 + space, COLOR_SHMUP_TEXT);
            int secondRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 2))).findRowCount();
            draw(graphics, secondRankCount, 60 + space, COLOR_SHMUP_TEXT);
            int thirdRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 3))).findRowCount();
            draw(graphics, thirdRankCount, 95 + space, COLOR_SHMUP_TEXT);
            int oneCreditCount = player.computeOneCredit();
            draw(graphics, oneCreditCount, 140 + space, COLOR_SHMUP_TEXT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi;
    }

    private static void draw(Graphics2D graphics, Integer count, int i, Color color) {
        graphics.setColor(color);
        graphics.setFont(gameFont);
        graphics.drawString(pad(count), i, HEIGHT - HEIGHT / 4);
    }

    private static String pad(Integer value) {
        return leftPad(value.toString(), 2);
    }

    public static BufferedImage createBlankImage() {
        return new BufferedImage(1, 1, TYPE_INT_ARGB);
    }
}
