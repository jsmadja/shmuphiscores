package controllers;

import models.Player;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import plugins.ShmupClient;

public class PlayerController extends Controller {

    public static Result index(Player player) {
        return ok(views.html.player_read.render(player));
    }

    public static Player current() {
        Http.Cookie userId = request().cookie("phpbb3_axtcz_u");
        if(userId == null) {
            return Player.guest;
        }
        Long shmupUserId = Long.parseLong(userId.value());
        if(shmupUserId == null) {
            return Player.guest;
        }
        Player player = Player.findByShmupUserId(shmupUserId);
        if(player == null) {
            ShmupClient shmupClient = new ShmupClient();
            String login = shmupClient.getLoginById(shmupUserId);
            player = Player.findOrCreatePlayer(login);
            player.shmupUserId = shmupUserId;
            player.update();
        }
        return player;
    }

}
