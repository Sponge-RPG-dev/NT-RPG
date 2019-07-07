package cz.neumimto.rpg.common.events;

import cz.neumimto.rpg.api.logging.Log;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import javax.inject.Singleton;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TestEventFactory extends EventFactoryImpl {


    @Override
    public <T> T createEventInstance(Class<? extends T> iFace) {
        if (cache.containsKey(iFace)) {
            return super.createEventInstance(iFace);
        }
        Map<String, Object> fields = new HashMap<>();
        @SuppressWarnings("unchecked")
        Class<T> proxyType = (Class<T>) new ByteBuddy()
                .subclass(Object.class)
                .implement(iFace)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(new MethodInterceptor(fields)))
                .make().load(iFace.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        try {
            return proxyType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Cannot initiate Event Proxy ");
    }

    @Override
    public void registerEventProviders() {

    }

    public class MethodInterceptor implements InvocationHandler {


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
