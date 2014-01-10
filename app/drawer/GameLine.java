package drawer;

import models.Difficulty;
import models.Mode;
import models.Ranking;

import java.awt.*;

import static drawer.RankingGameConfiguration.COLOR_SHMUP_TITLE;
import static drawer.RankingGameConfiguration.width;
import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;

public class GameLine implements PictureLine {

    private final static Font gameFont = new Font("Liberation Mono", BOLD | ITALIC, 16);
    private final static int titleFontHeight = 20;

    private final Mode mode;
    private final Difficulty difficulty;

    public GameLine(Ranking ranking) {
        this.mode = ranking.mode;
        this.difficulty = ranking.difficulty;
    }

    @Override
    public void draw(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
        graphics.fillRect(0, y - titleFontHeight, width, y);
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