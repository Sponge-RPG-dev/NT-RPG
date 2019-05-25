package cz.neumimto.rpg.common.events;

import cz.neumimto.rpg.api.events.effect.EventFactoryService;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
public class EventFactoryImpl implements EventFactoryService {

    private Map<Class<?>, Supplier<?>> cache = new HashMap<>();

    @Override
    public <T> T createEventInstance(Class<? extends T> clazz) {
        return (T) cache.get(clazz).get();
    }

    @Override
    public void registerProvider(Class<?> clazz, Supplier<?> provider) {
        cache.put(clazz, provider);
    }

    @Override
    public <T> void registerProvider(Class<T> clazz, Class<? extends T> implementation) {
        Supplier<T> supplier = () -> {
            try {
                return implementation.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Could not find default constructor for " + implementation.getName());
            }
        };
        registerProvider(clazz, supplier);
    }

}
