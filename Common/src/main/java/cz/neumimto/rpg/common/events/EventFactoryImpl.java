package cz.neumimto.rpg.common.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class EventFactoryImpl implements EventFactoryService {

    protected Map<Class<?>, Supplier<?>> cache = new HashMap<>();

    @Override
    public <T> T createEventInstance(Class<? extends T> clazz) {
        return (T) cache.get(clazz).get();
    }

    @Override
    public <T> void registerProvider(Class<T> clazz, Supplier<? extends T> provider) {
        cache.put(clazz, provider);
    }


}
