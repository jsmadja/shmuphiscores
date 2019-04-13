package drawer;

import com.avaje.ebean.Ebean;
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
import static java.awt.Font.BOLD;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.apache.commons.lang3.StringUtils.leftPad;

public class MedalsPicture {

    private final static Font gameFont = new Font("Verdana", BOLD, 11);
    private final static Font scoreFont = new Font("Verdana", BOLD, 14);

    public static BufferedImage createMedalsPicture(Player player) {
        try {
            BufferedImage bi = ImageIO.read(new File("public/images/medailles.png"));
            Graphics2D graphics = bi.createGraphics();
            graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

            int space = 3;
            Integer firstRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 1))).findRowCount();
            draw(graphics, firstRankCount, 25 + space);
            int secondRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 2))).findRowCount();
            draw(graphics, secondRankCount, 60 + space);
            int thirdRankCount = find(Score.class).where(and(eq("player", player), eq("rank", 3))).findRowCount();
            draw(graphics, thirdRankCount, 95 + space);
            int oneCreditCount = player.computeOneCredit();
            draw(graphics, oneCreditCount, 140 + space);
            drawBelow(graphics, Ebean.createSqlQuery("select distinct game_id from score where player_id=:player_id").setParameter("player_id", player.id).findList().size() + " jeux scorés");
            return bi;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void draw(Graphics2D graphics, Integer count, int i) {
        graphics.setColor(RankingGameConfiguration.COLOR_SHMUP_TEXT);
        graphics.setFont(gameFont);
        graphics.drawString(pad(count), i, 25);
    }

    private static void drawBelow(Graphics2D graphics, String text) {
        graphics.setColor(RankingGameConfiguration.COLOR_SCORE_TEXT);
        graphics.setFont(scoreFont);
        graphics.drawString(text, 10, 45);
    }

    private static String pad(Integer value) {
        return leftPad(value.toString(), 2);
    }

    public static BufferedImage blankImage = new BufferedImage(1, 1, TYPE_INT_ARGB);
}
