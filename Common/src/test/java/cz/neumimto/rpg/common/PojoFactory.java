package cz.neumimto.rpg.common;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PojoFactory {

    static Map<Class, Class> cache = new ConcurrentHashMap<>();

    public static Class create(Class iFace) {
        if (cache.containsKey(iFace)) {
            return cache.get(iFace);
        }
        Map<String, Object> fields = new HashMap<>();
        Class<?> loaded = new ByteBuddy()
                .subclass(Object.class)
                .implement(iFace)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(new MethodInterceptor(fields)))
                .make().load(iFace.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        cache.put(iFace, loaded);
        return loaded;
    }

    public static Object createInstance(Class iFace)  {
        try {
            return create(iFace).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static class MethodInterceptor implements InvocationHandler {


        private Map<String, Object> fields;

        public MethodInterceptor(Map<String, Object> fields) {
            this.fields = fields;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().startsWith("set")) {
                fields.put(method.getName().substring(3), args[0]);
            }
            if (method.getName().startsWith("get")) {
                return fields.get(method.getName().substring(3));
            }
            return null;
        }
    }
}
