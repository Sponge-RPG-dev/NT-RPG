package cz.neumimto.rpg.api.events.effect;

import java.util.function.Supplier;

public interface EventFactoryService {
    <T> T createEventInstance(Class<? extends T> clazz);

    void registerProvider(Class<?> clazz, Supplier<?> provider);

    <T> void registerProvider(Class<T> clazz, Class<? extends T> implementation);
}
