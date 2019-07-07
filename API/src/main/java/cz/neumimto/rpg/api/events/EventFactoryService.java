package cz.neumimto.rpg.api.events;

import java.util.function.Supplier;

public interface EventFactoryService {

    <T> T createEventInstance(Class<? extends T> clazz);

    <T> void registerProvider(Class<T> clazz, Supplier<? extends T> provider);

    void registerEventProviders();

}
