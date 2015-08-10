package controllers;

import drawer.MedalsPicture;
import drawer.SignaturePicture;
import models.Player;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerController extends Controller {

    public static Result index(Player player) {
        if (player == null) {
            return notFound();
        }
        return ok(views.html.player_read.render(player));
    }

    public static Map<Player, byte[]> getSignatureCache() {
        Map<Player, byte[]> signatures = (Map<Player, byte[]>) Cache.get("signatureCache");
        if (signatures == null) {
            signatures = new HashMap<Player, byte[]>();
            Cache.set("signatureCache", signatures, 3600);
        }
        return signatures;
    }

    public static Map<Player, byte[]> getMedalsCache() {
        Map<Player, byte[]> medals = (Map<Player, byte[]>) Cache.get("medalsCache");
        if (medals == null) {
            medals = new HashMap<Player, byte[]>();
            Cache.set("medalsCache", medals, 3600);
        }
        return medals;
    }

    public static Result signature(Player player) throws IOException {
        if (player == null) {
            return notFound();
        }
        Map<Player, byte[]> signatures = getSignatureCache();
        byte[] bytes = signatures.get(player);
        if (bytes == null) {
            BufferedImage image = SignaturePicture.createSignaturePicture(player);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);
            bytes = stream.toByteArray();
            signatures.put(player, bytes);
        }
        response().setHeader(CACHE_CONTROL, "max-age=3600");
        response().setContentType("image/png");
        return ok(bytes);
    }

    public static Result medals(Long shmupId) throws IOException {
        Player player = Player.findByShmupUserId(shmupId);
        if (player == null) {
            return notFound();
        }
        Map<Player, byte[]> medals = getMedalsCache();
        byte[] bytes = medals.get(player);
        if (bytes == null) {
            BufferedImage image = MedalsPicture.createMedalsPicture(player);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);
            bytes = stream.toByteArray();
            medals.put(player, bytes);
        }
        response().setHeader(CACHE_CONTROL, "max-age=3600");
        response().setContentType("image/png");
        return ok(bytes);
    }

}
