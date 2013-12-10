package actions;

import models.Player;
import org.apache.commons.lang3.time.StopWatch;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import plugins.ShmupClient;

import java.lang.reflect.Method;

public class User extends Action.Simple {

    private StopWatch stopWatch;
    private Method actionMethod;

    public User(Method actionMethod) {
        this.actionMethod = actionMethod;
    }

    @Override
    public F.Promise<SimpleResult> call(Http.Context context) throws Throwable {
        startWatch();
        context.args.put("user", getPlayerFromCookie(context));
        F.Promise<SimpleResult> call = delegate.call(context);
        stopWatch();
        Logger.info(context.request().uri() + " " + stopWatch.getTime() + "ms");
        return call;
    }

    private void stopWatch() {
        stopWatch.stop();
    }

    private void startWatch() {
        stopWatch = new StopWatch();
        stopWatch.start();
    }

    public static Player current() {
        return (Player) Http.Context.current().args.get("user");
    }

    private static Player getPlayerFromCookie(Http.Context context) {
        Long shmupUserId;
        if (Play.isDev()) {
            shmupUserId = 33489L;
        } else {
            Http.Cookie userId = context.request().cookie("phpbb3_axtcz_u");
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