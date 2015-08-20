package controllers;

import drawer.Images;
import drawer.MedalsPicture;
import drawer.SignaturePicture;
import models.Player;
import play.mvc.Controller;
import play.mvc.Result;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import static drawer.MedalsPicture.createMedalsPicture;

public class PlayerController extends Controller {

    public static Result index(Player player) {
        if (player == null) {
            return notFound();
        }
        return ok(views.html.player_read.render(player, player.fetchScores()));
    }

    public static Result signature(Player player) throws IOException {
        if (player == null) {
            return notFound();
        }
        Map<Player, byte[]> signatures = CacheController.getSignatureCache();
        byte[] bytes = signatures.get(player);
        if (bytes == null) {
            BufferedImage image = SignaturePicture.createSignaturePicture(player);
            bytes = Images.toBytes(image);
            signatures.put(player, bytes);
        }
        response().setHeader(CACHE_CONTROL, "max-age=3600");
        response().setContentType("image/png");
        return ok(bytes);
    }

    public static Result medals(Long shmupId) throws IOException {
        response().setHeader(CACHE_CONTROL, "max-age=3600");
        response().setContentType("image/png");
        Player player = Player.findByShmupUserId(shmupId);
        if (player == null) {
            return ok(Images.toBytes(MedalsPicture.blankImage));
        }
        Map<Player, byte[]> medals = CacheController.getMedalsCache();
        byte[] bytes = medals.get(player);
        if (bytes == null) {
            BufferedImage image = createMedalsPicture(player);
            bytes = Images.toBytes(image);
            medals.put(player, bytes);
        }
        return ok(bytes);
    }

    public static Result indexShmup(Long shmupId) throws IOException {
        Player player = Player.findByShmupUserId(shmupId);
        return index(player);
    }

}
