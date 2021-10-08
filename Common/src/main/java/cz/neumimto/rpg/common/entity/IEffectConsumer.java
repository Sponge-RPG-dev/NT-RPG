

package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.common.effects.EffectContainer;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;

import java.util.Collection;
import java.util.Map;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public interface IEffectConsumer extends PropertyContainer {

    Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap();

    default Collection<IEffectContainer<Object, IEffect<Object>>> getEffects() {
        return getEffectMap().values();
    }

    default IEffectContainer getEffect(String cl) {
        return getEffectMap().get(cl);
    }

    default boolean hasEffect(String cl) {
        return getEffectMap().containsKey(cl);
    }

    @SuppressWarnings("unchecked")
    default void addEffect(IEffect effect) {
        IEffectContainer iEffectContainer1 = getEffectMap().get(effect.getName());
        if (iEffectContainer1 == null) {
            getEffectMap().put(effect.getName(), new EffectContainer<>(effect));
        } else {
            iEffectContainer1.getEffects().add(effect);
        }
    }

    @SuppressWarnings("unchecked")
    default void addEffect(IEffectContainer<Object, IEffect<Object>> iEffectContainer) {
        IEffectContainer effectContainer1 = getEffectMap().get(iEffectContainer.getName());
        if (effectContainer1 == null) {
            getEffectMap().put(iEffectContainer.getName(), iEffectContainer);
        } else {
            effectContainer1.mergeWith(iEffectContainer);
        }
    }

    default void removeEffect(String cl) {
        getEffectMap().remove(cl);
    }

    default void removeEffect(IEffectContainer cl) {
        getEffectMap().remove(cl.getName());
    }

    default void removeEffect(IEffect cl) {
        getEffectMap().remove(cl.getName());
    }

    boolean isDetached();

    Object getEntity();
}
