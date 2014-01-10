package controllers;

import models.Game;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;
import drawer.RankingPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RankingController extends Controller {

    public static Map<Game, byte[]> getRankingCache() {
        Map<Game, byte[]> rankings = (Map<Game, byte[]>) Cache.get("rankingCache");
        if (rankings == null) {
            rankings = new HashMap<Game, byte[]>();
            Cache.set("rankingCache", rankings);
        }
        return rankings;
    }

    public static Result index(models.Game game) throws IOException {
        if(game == null) {
            return notFound();
        }
        if(request().getQueryString("refresh") != null) {
            getRankingCache().remove(game);
        }
        Map<Game, byte[]> rankings = getRankingCache();
        byte[] bytes = rankings.get(game);
        if (bytes == null) {
            BufferedImage image = RankingPicture.createRankingPicture(game);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);
            bytes = stream.toByteArray();
            rankings.put(game, bytes);
        }
        response().setContentType("image/png");
        return ok(bytes);
    }

}
