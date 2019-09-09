package cz.neumimto.rpg.api;

import com.google.inject.Injector;

import java.util.Map;

public interface RpgAddon {

    Map<Class<?>, Class<?>> getBindings();

    default void processStageEarly(Injector injector) {}

    default void processStageLate(Injector injector) {}

    Map<Class<?>, ?> getProviders(Map<String, Object> implementationScope);
}
