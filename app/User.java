import org.apache.commons.lang3.time.StopWatch;
import play.Logger;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

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

}
