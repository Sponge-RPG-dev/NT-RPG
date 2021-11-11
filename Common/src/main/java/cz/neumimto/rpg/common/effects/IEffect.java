

package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.entity.IEffectConsumer;

import java.util.Set;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public interface IEffect<K> extends IRpgElement {

    /**
     * @param self The reference to the effect currently being processed. IE: self == this is always true. Useful mainly for JavaScript scripts
     */
    default void onTick(IEffect self) {

    }

    /**
     * @param self The reference to the effect currently being processed. IE: self == this is always true. Useful mainly for JavaScript scripts
     */
    default void onApply(IEffect self) {

    }

    /**
     * @param self The reference to the effect currently being processed. IE: self == this is always true. Useful mainly for JavaScript scripts
     */
    default void onRemove(IEffect self) {

    }

    String getName();

    boolean isStackable();

    void setStackable(boolean b, EffectStackingStrategy<K> stackingStrategy);

    boolean requiresRegister();

    long getPeriod();

    void setPeriod(long period);

    long getLastTickTime();

    void setLastTickTime(long currTime);

    long getExpireTime();

    long getDuration();

    void setDuration(long l);

    IEffectConsumer getConsumer();

    Set<EffectType> getEffectTypes();

    IEffectSourceProvider getEffectSourceProvider();

    void setEffectSourceProvider(IEffectSourceProvider effectSourceProvider);

    K getValue();

    void setValue(K k);

    default <T extends IEffect<K>> IEffectContainer<K, T> constructEffectContainer() {
        return new EffectContainer(this);
    }

    EffectStackingStrategy<K> getEffectStackingStrategy();

    void setEffectStackingStrategy(EffectStackingStrategy<K> effectStackingStrategy);

    IEffectContainer<K, IEffect<K>> getEffectContainer();

    void setEffectContainer(IEffectContainer<K, IEffect<K>> iEffectContainer);

    boolean isTickingDisabled();

    void setTickingDisabled(boolean tickingDisabled);

}
