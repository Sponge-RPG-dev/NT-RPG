package cz.neumimto.effects;

/**
 * Created by NeumimTo.
 */
public interface IGlobalEffect<T extends IEffect> {
    T construct(IEffectConsumer consumer, long duration, int level);

    String getName();
}
