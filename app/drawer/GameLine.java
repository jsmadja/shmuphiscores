package drawer;

import models.Difficulty;
import models.Mode;
import models.Ranking;
import models.Ship;
import play.i18n.Messages;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static drawer.RankingGameConfiguration.COLOR_SHMUP_TITLE;
import static drawer.RankingGameConfiguration.width;
import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;

public class GameLine implements PictureLine {

    private final static Font gameFont = new Font("Liberation Mono", BOLD | ITALIC, 16);
    private final static int titleFontHeight = 20;

    private final Mode mode;
    private final Difficulty difficulty;
    private final Ranking ranking;

    public GameLine(Ranking ranking) {
        this.mode = ranking.mode;
        this.difficulty = ranking.difficulty;
        this.ranking = ranking;
    }

    @Override
    public void draw(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
        graphics.fillRect(0, y - titleFontHeight, width, y);
        graphics.setColor(COLOR_SHMUP_TITLE);
        graphics.setFont(gameFont);
        String title = "";
        if (ranking.general) {
            title += Messages.get("GeneralRanking");
        }
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