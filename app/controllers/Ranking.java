package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import plugins.RankingPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.google.common.io.Files.toByteArray;

public class Ranking extends Controller {

    public static Result index(models.Game game) throws IOException {
        BufferedImage bi = RankingPicture.createRankingPicture(game);
        File dest = new File("/tmp/pouet");
        ImageIO.write(bi, "PNG", dest);
        byte[] bytes = toByteArray(dest);
        response().setContentType("image/png");
        return ok(bytes);
    }

}
