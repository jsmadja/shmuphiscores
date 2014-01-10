package models;

import com.avaje.ebean.event.BeanPersistAdapter;

import javax.persistence.PostLoad;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ShmupBeanPersistAdapter extends BeanPersistAdapter {

    private Map<String, Method> postLoadMap = new TreeMap<String, Method>();

    @Override
    public boolean isRegisterFor(Class<?> aClass) {
        if (aClass.getAnnotation(javax.persistence.Entity.class) != null) {
            Method[] methods = aClass.getMethods();
            boolean hasListener = false;
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostLoad.class)) {
                    postLoadMap.put(aClass.getName(), method);
                    hasListener = true;
                }
            }
            return hasListener;
        }
        return false;
    }

    @Override
    public void postLoad(Object bean, Set<String> includedProperties) {
        getAndInvokeMethod(postLoadMap, bean);
        super.postLoad(bean, includedProperties);
    }

    private void getAndInvokeMethod(Map<String, Method> map, Object o) {
        Method m = map.get(o.getClass().getName());
        if(m != null) {
            try {
                m.invoke(o);
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
