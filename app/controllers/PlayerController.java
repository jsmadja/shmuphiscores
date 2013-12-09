package controllers;

import models.Player;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import plugins.ShmupClient;

public class PlayerController extends Controller {

    public static Result index(Player player) {
        if(player == null) {
            return notFound();
        }
        return ok(views.html.player_read.render(player));
    }

    public static Player current() {
        Long shmupUserId;
        if (Play.isDev()) {
            shmupUserId = 33489L;
        } else {
            Http.Cookie userId = request().cookie("phpbb3_axtcz_u");
            if(userId == null || userId.value().equals("1")) {
                return Player.guest;
            }
            shmupUserId = Long.parseLong(userId.value());
        }
        Player player = Player.findByShmupUserId(shmupUserId);
        if (player == null) {
            ShmupClient shmupClient = new ShmupClient();
            String login = shmupClient.getLoginById(shmupUserId);
            player = Player.findOrCreatePlayer(login);
            player.shmupUserId = shmupUserId;
            player.update();
        }
        //Logger.info("phpbb3_axtcz_u = " + player.name);
        return player;
    }

}
