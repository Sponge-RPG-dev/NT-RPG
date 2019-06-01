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
import java.util.function.Supplier;

@Singleton
public class TestEventFactory extends EventFactoryImpl {


    @Override
    public <T> T createEventInstance(Class<? extends T> iFace) {
        if (cache.containsKey(iFace)) {
            return super.createEventInstance(iFace);
        }

        @SuppressWarnings("unchecked")
        Class<T> proxyType = (Class<T>) new ByteBuddy()
                .subclass(Object.class)
                .implement(iFace)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(new MethodInterceptor()))
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


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.info(method.getName());
            return null;
        }
    }
}
