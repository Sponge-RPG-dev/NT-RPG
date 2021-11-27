package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.common.entity.IEffectConsumer;

import java.util.Map;

/**
 * Created by NeumimTo.
 */
public interface IGlobalEffect<T extends IEffect> {

    T construct(IEffectConsumer consumer, long duration, Map<String, String> data);

    String getName();

    Class<T> asEffectClass();
}
