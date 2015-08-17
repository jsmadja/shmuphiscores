package controllers;

import drawer.Images;
import drawer.RankingPicture;
import models.Game;
import play.mvc.Controller;
import play.mvc.Result;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public class RankingController extends Controller {

    public static Result index(models.Game game) throws IOException {
        if (game == null) {
            return notFound();
        }
        if (request().getQueryString("refresh") != null) {
            CacheController.getRankingCache().remove(game);
        }
        Map<Game, byte[]> rankings = CacheController.getRankingCache();
        byte[] bytes = rankings.get(game);
        if (bytes == null) {
            BufferedImage image = RankingPicture.createRankingPicture(game);
            bytes = Images.toBytes(image);
            rankings.put(game, bytes);
        }
        response().setContentType("image/png");
        return ok(bytes);
    }

}
