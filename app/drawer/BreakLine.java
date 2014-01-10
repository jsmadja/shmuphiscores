package drawer;

import java.awt.*;

import static drawer.RankingGameConfiguration.COLOR_SHMUP_GREY;
import static drawer.RankingGameConfiguration.fontHeight;
import static drawer.RankingGameConfiguration.width;

public class BreakLine implements PictureLine {

    @Override
    public void draw(Graphics2D graphics, int y, RankingGameConfiguration rankingGameConfiguration) {
        graphics.setColor(COLOR_SHMUP_GREY);
        graphics.fillRect(0, y - fontHeight, width, y);
        graphics.drawString("", 0, y);
    }
}