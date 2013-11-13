package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import plugins.RankingPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Ranking extends Controller {

    public static Result index(models.Game game) throws IOException {
        BufferedImage image = RankingPicture.createRankingPicture(game);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", stream);
        byte[] bytes = stream.toByteArray();
        response().setContentType("image/png");
        return ok(bytes);
    }

}
