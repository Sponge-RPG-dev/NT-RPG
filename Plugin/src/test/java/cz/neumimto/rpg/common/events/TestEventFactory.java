package cz.neumimto.rpg.common.events;

import cz.neumimto.rpg.api.logging.Log;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import javax.inject.Singleton;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Singleton
public class TestEventFactory extends EventFactoryImpl {


    @Override
    public <T> T createEventInstance(Class<? extends T> iFace) {
        if (cache.containsKey(iFace)) {
            return super.createEventInstance(iFace);
        }

        Class<?> proxyType = new ByteBuddy()
                .subclass(Object.class)
                .implement(iFace)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(new MethodInterceptor()))
                .make().load(iFace.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();


        registerProvider(iFace, () -> {
            try {
                return proxyType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("");
        });


        return super.createEventInstance(iFace);
    }

    public class MethodInterceptor implements InvocationHandler {


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.info(method.getName());
            return null;
        }
    }
}
