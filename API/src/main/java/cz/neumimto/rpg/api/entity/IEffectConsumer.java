/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.effects.EffectContainer;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;

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
