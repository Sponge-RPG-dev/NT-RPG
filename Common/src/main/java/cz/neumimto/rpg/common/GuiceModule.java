package cz.neumimto.rpg.common;

import java.util.Map;

public interface GuiceModule {

    Map<Class<?>, Class<?>> getBindings();

    default void processStageEarly(){};

    default void processStageLate() {}
}
