package controllers;

import drawer.RankingGameConfiguration;
import drawer.SignaturePicture;
import models.Player;
import play.mvc.Controller;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static drawer.MedalsPicture.createBlankImage;
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
            bytes = toBytes(image);
            signatures.put(player, bytes);
        }
        response().setHeader(CACHE_CONTROL, "max-age=3600");
        response().setContentType("image/png");
        return ok(bytes);
    }

    public static Result medals(Long shmupId) throws IOException {
        return medalsWithColor(shmupId);
    }

    private static Result medalsWithColor(Long shmupId) throws IOException {
        response().setHeader(CACHE_CONTROL, "max-age=3600");
        response().setContentType("image/png");
        Player player = Player.findByShmupUserId(shmupId);
        if (player == null) {
            return ok(toBytes(createBlankImage()));
        }
        Map<Player, byte[]> medals = CacheController.getMedalsCache();
        byte[] bytes = medals.get(player);
        if (bytes == null) {
            BufferedImage image = createMedalsPicture(player);
            bytes = toBytes(image);
        }
        return ok(bytes);
    }

    public static Result medalsWhite(Long shmupId) throws IOException {
        return medalsWithColor(shmupId);
    }

    private static byte[] toBytes(BufferedImage image) throws IOException {
        byte[] bytes;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", stream);
        bytes = stream.toByteArray();
        return bytes;
    }

}
