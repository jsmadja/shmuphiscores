package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.io.Files.toByteArray;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class Ranking extends Controller {

    public static Result index() throws IOException {
        BufferedImage bi = createImage();
        File dest = new File("/tmp/pouet");
        ImageIO.write(bi, "PNG", dest);
        byte[] bytes = toByteArray(dest);
        response().setContentType("image/png");
        return ok(bytes);
    }

    private static BufferedImage createImage() {
        List<String[]> lines = new ArrayList<String[]>();
        lines.add(new String[]{"1.", "Gwürz!", "6.562.300 pts - Stage 1 (408%) - Loop 5 - (PCB Set 2)"});

        //        {"2."}, {"DIDYEAH"},{"3.030.230 pts - Stage 3 (208%) - Loop 3 - (PCB Set 2)"},
        //        {"3."}, {"fdsfdsfds"}, {""}

        int fontHeight = 13;
        int width = 500, height = fontHeight * lines.size();
        BufferedImage bi = new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.setColor(new Color(240, 243, 244));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);
        Font normalFont = new Font("Verdana", Font.PLAIN, 12);
        Font playerFont = new Font("Verdana", Font.BOLD, 12);
        graphics.setFont(normalFont);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int stringHeight = fontMetrics.getAscent();
        for (int i = 0; i < lines.size(); i++) {
            graphics.setPaint(Color.BLACK);
            String[] line = lines.get(i);
            String rank = line[0];
            String player = line[1];
            String score = line[2];
            graphics.drawString(rank, 0, 10 + (i * stringHeight));

            graphics.setFont(playerFont);
            graphics.drawString(player, (rank.length() * 10) + 10, 10 + (i * stringHeight));

            graphics.setColor(Color.ORANGE);
            graphics.drawString(score, (rank.length() * 10) + 10 + (player.length() * 10), 10 + (i * stringHeight));
        }
        return bi;
    }

}
