package cz.neumimto.rpg.common.events;

import cz.neumimto.rpg.common.PojoFactory;

import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;

@Singleton
public class TestEventFactory extends EventFactoryImpl {

    @Override
    public <T> T createEventInstance(Class<? extends T> iFace) {
        Class<T> proxyType = PojoFactory.create(iFace);

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


}
