import actions.User;
import play.Application;
import play.GlobalSettings;
import play.mvc.Action;
import play.mvc.Http;

import java.lang.reflect.Method;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
    }

    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        return new User(actionMethod);
    }

}