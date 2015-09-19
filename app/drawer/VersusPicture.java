package drawer;

import models.Player;
import models.Versus;

import java.awt.*;
import java.awt.image.BufferedImage;

import static drawer.RankingGameConfiguration.COLOR_SHMUP_TEXT;
import static java.awt.Font.PLAIN;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.String.format;

public class VersusPicture {
    public static final int WIDTH = 724;
    private final static Font gameFont = new Font("Lucida", PLAIN, 11);

    public static BufferedImage createVersusPicture(Player player) {
        BufferedImage bi = new BufferedImage(WIDTH, 15, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.setColor(COLOR_SHMUP_TEXT);
        graphics.setFont(gameFont);
        graphics.drawString(message(player), 0, fontMetrics.getAscent());
        return bi;
    }

    private static String message(Player player) {
        Versus versus = player.getBestVersus();
        return format("Adversaire privilégié : %s (ratio: %d/%d)", versus.player2.name, versus.winCount(), versus.loseCount());
    }
}
