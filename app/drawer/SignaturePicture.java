package drawer;

import models.Player;
import models.Score;

import java.awt.*;
import java.awt.image.BufferedImage;

import static drawer.RankingGameConfiguration.COLOR_SHMUP_GREY;
import static drawer.RankingGameConfiguration.COLOR_SHMUP_TITLE;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class SignaturePicture {
    private final static Font gameFont = new Font("Liberation Mono", Font.PLAIN, 11);

    public static BufferedImage createSignaturePicture(Player player) {
        BufferedImage bi = new BufferedImage(724, 30, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fontMetrics = graphics.getFontMetrics();

        graphics.setColor(COLOR_SHMUP_GREY);
        graphics.fillRect(0, 0, 724, fontMetrics.getAscent() + 5);
        graphics.setColor(COLOR_SHMUP_TITLE);
        graphics.setFont(gameFont);
        Score lastScore = player.getLastScore();
        graphics.drawString(message(lastScore), 5, fontMetrics.getAscent());
        return bi;
    }

    private static String message(Score lastScore) {
        return "Mon dernier score a été réalisé " + lastScore.formattedDate() + " sur " + lastScore.game.title + " (" + lastScore.formattedValue() + "pts - " + lastScore.formattedRank() + ")";
    }
}
