package cz.neumimto.rpg.common;

import com.google.inject.Injector;

import java.util.Map;

public interface GuiceModule {

    Map<Class<?>, Class<?>> getBindings();

    default void processStageEarly(Injector injector){};

    default void processStageLate(Injector injector) {}
}
