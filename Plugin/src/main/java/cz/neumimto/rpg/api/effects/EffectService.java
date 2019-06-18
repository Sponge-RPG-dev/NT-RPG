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

package cz.neumimto.rpg.api.effects;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.effects.InternalEffectSourceProvider;

import java.util.*;
import java.util.function.Consumer;


/**
 * Created by NeumimTo on 17.1.2015.
 */
public abstract class EffectService {

    public static final long TICK_PERIOD = 5L;

    private static final long unlimited_duration = -1;

    protected Set<IEffect> effectSet = new HashSet<>();
    protected Set<IEffect> pendingAdditions = new HashSet<>();
    protected Set<IEffect> pendingRemovals = new HashSet<>();
    protected Map<String, IGlobalEffect> globalEffects = new HashMap<>();


    /**
     * calls effect.onApply and registers if effect requires
     *
     * @param effect
     */
    protected void runEffect(IEffect effect) {
        pendingAdditions.add(effect);
    }

    /**
     * Puts the effect into the remove queue. onRemove will be called one tick later
     *
     * @param effect
     */
    public void stopEffect(IEffect effect) {
        if (effect.requiresRegister()) {
            pendingRemovals.add(effect);
        }
    }


    public abstract void load();

    public abstract void startEffectScheduler();

    public void schedule() {
        for (IEffect pendingRemoval : pendingRemovals) {
            removeEffectContainer(pendingRemoval.getEffectContainer(), pendingRemoval, pendingRemoval.getConsumer());
            effectSet.remove(pendingRemoval);
        }

        pendingRemovals.clear();
        long l = System.currentTimeMillis();
        for (IEffect e : effectSet) {
            if (!mayTick(e)) {
                pendingRemovals.add(e);
                continue;
            }
            if ((e.getPeriod() > 0 && !e.isTickingDisabled()) && e.getPeriod() + e.getLastTickTime() <= l) {
                tickEffect(e, l);
            }

            if (e.getDuration() == unlimited_duration) {
                continue;
            }

            if (e.getExpireTime() <= l) {
                removeEffect(e, e.getConsumer());
            }
        }

        effectSet.addAll(pendingAdditions);
        pendingAdditions.clear();
    }

    protected abstract boolean mayTick(IEffect e);

    /**
     * Calls onTick and increments tickCount
     *
     * @param effect
     */
    public void tickEffect(IEffect effect, long time) {
        effect.onTick(effect);
        effect.setLastTickTime(time);
    }

    /**
     * Adds effect to the consumer,
     * Effects requiring register are registered into the scheduler one tick later
     *
     * @param effect effect
     * @return true if effect is successfully applied
     */
    public <T extends IEffect> boolean addEffect(T effect) {
        return addEffect(effect, InternalEffectSourceProvider.INSTANCE);
    }
    /**
     * Adds effect to the consumer,
     * Effects requiring register are registered into the scheduler one tick later
     *
     * @param effect               effect
     * @param effectSourceProvider source
     * @return true if effect is successfully applied
     */
    public <T extends IEffect> boolean addEffect(T effect, IEffectSourceProvider effectSourceProvider) {
        return addEffect(effect, effectSourceProvider, null);
    }

    /**
     * Adds effect to the consumer,
     * Effects requiring register are registered into the scheduler one tick later
     *
     * @param effect               effect
     * @param effectSourceProvider source
     * @param entitySource         caster of effect
     * @return true if effect is successfully applied
     */
    @SuppressWarnings("unchecked")
    public <T extends IEffect> boolean addEffect(T effect, IEffectSourceProvider effectSourceProvider, IEntity entitySource) {
        IEffectContainer eff = effect.getConsumer().getEffect(effect.getName());
        if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
            IEffectConsumer consumer1 = effect.getConsumer();
            if (consumer1 instanceof IActiveCharacter) {
                IActiveCharacter chara = (IActiveCharacter) consumer1;
                chara.sendMessage("Adding effect: " + effect.getName() +
                        " container: " + (eff == null ? "null" : eff.getEffects().size()) +
                        " provider: " + effectSourceProvider.getType().getClass().getSimpleName());
            }
        }
        if (eff == null) {
            eff = effect.constructEffectContainer();
            effect.getConsumer().addEffect(eff);
            effect.onApply(effect);
        } else if (eff.isStackable()) {
            eff.stackEffect(effect, effectSourceProvider);
        } else {
            eff.forEach((Consumer<IEffect>) this::stopEffect); //there should be always only one
            //on remove will be called one tick later.
            eff.getEffects().add(effect);
            effect.onApply(effect);
        }

        effect.setEffectContainer(eff);
        if (effect.requiresRegister()) {
            runEffect(effect);
        }

        return true;
    }

    /**
     * Removes effect from IEffectConsumer, and stops it. The effect will be removed from the scheduler next tick
     *
     * @param effect
     * @param consumer
     */
    public void removeEffect(IEffect effect, IEffectConsumer consumer) {
        IEffectContainer container = consumer.getEffect(effect.getName());
        if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
            IEffectConsumer consumer1 = effect.getConsumer();
            if (consumer1 instanceof IActiveCharacter) {
                IActiveCharacter chara = (IActiveCharacter) consumer1;
                chara.sendMessage("Removing effect: " + effect.getName() +
                        " container: " + (container == null ? "null" : container.getEffects().size()));
            }
        }
        if (container != null) {
            removeEffectContainer(container, effect, consumer);
            stopEffect(effect);
        }
    }

    public <T, E extends IEffect<T>> void removeEffectContainer(IEffectContainer<T, E> container, IEffectConsumer consumer) {
        container.forEach(a -> removeEffect(a, consumer));
    }

    protected void removeEffectContainer(IEffectContainer container, IEffect effect, IEffectConsumer consumer) {
        if (effect == container) {
            if (!effect.getConsumer().isDetached()) {
                effect.onRemove(effect);
                consumer.removeEffect(effect);
            }
        } else if (container.getEffects().contains(effect)) {
            container.removeStack(effect);
            if (container.getEffects().isEmpty()) {
                if (consumer != null) {
                    consumer.removeEffect(container);
                }
            } else {
                container.updateStackedValue();
            }
        } else {

        }
        effect.setConsumer(null);
    }

    /**
     * Removes and stops the effect previously applied as item enchantement
     *
     * @param iEffect
     * @param consumer
     */
    @SuppressWarnings("unchecked")
    public void removeEffect(String iEffect, IEffectConsumer consumer, IEffectSourceProvider effectSource) {
        IEffectContainer effect = consumer.getEffect(iEffect);
        if (effect != null) {
            Iterator<IEffect> iterator = effect.getEffects().iterator();
            IEffect e;
            while (iterator.hasNext()) {
                e = iterator.next();
                if (e.getEffectSourceProvider() == effectSource) {
                    removeEffectContainer(effect, e, consumer);
                    stopEffect(e);
                }
            }
        }
    }

    /**
     * Register global effect
     *
     * @param iGlobalEffect
     */
    public void registerGlobalEffect(IGlobalEffect iGlobalEffect) {
        globalEffects.put(iGlobalEffect.getName().toLowerCase(), iGlobalEffect);
    }

    /**
     * Removes cached globaleffect
     *
     * @param name
     */
    public void removeGlobalEffect(String name) {
        name = name.toLowerCase();
        globalEffects.remove(name);
    }

    /**
     * Returns global effect by its name, if effect does not exists return null
     *
     * @param name
     * @return effect or null if key is not in the map
     */
    public IGlobalEffect getGlobalEffect(String name) {
        return globalEffects.get(name.toLowerCase());
    }

    public Map<String, IGlobalEffect> getGlobalEffects() {
        return globalEffects;
    }

    /**
     * Applies global effect with unlimited duration
     *
     * @param effect
     * @param consumer
     * @param value
     */
    public void applyGlobalEffectAsEnchantment(IGlobalEffect effect, IEffectConsumer consumer, Map<String, String> value,
                                               IEffectSourceProvider effectSourceType) {
        IEffect construct = effect.construct(consumer, unlimited_duration, value);
        addEffect(construct, effectSourceType);
    }

    /**
     * Applies global effect with unlimited duration
     *
     * @param map
     * @param consumer
     */
    public void applyGlobalEffectsAsEnchantments(Map<IGlobalEffect, EffectParams> map, IEffectConsumer consumer,
                                                 IEffectSourceProvider effectSourceType) {
        map.forEach((e, l) ->
                applyGlobalEffectAsEnchantment(e, consumer, l, effectSourceType)
        );
    }


    public void removeGlobalEffectsAsEnchantments(Collection<IGlobalEffect> itemEffects, IActiveCharacter character,
                                                  IEffectSourceProvider effectSourceProvider) {
        if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
            character.sendMessage(itemEffects.size() + " added echn. effect to remove queue.");
        }
        itemEffects.forEach((e) -> {
            removeEffect(e.getName(), character, effectSourceProvider);
        });
    }


    public boolean isGlobalEffect(String s) {
        return globalEffects.containsKey(s.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    /**
     * Called only in cases when entities dies, or players logs off
     */
    public void removeAllEffects(IEffectConsumer character) {
        Iterator<IEffectContainer<Object, IEffect<Object>>> iterator1 = character.getEffects().iterator();
        while (iterator1.hasNext()) {
            IEffectContainer<Object, IEffect<Object>> next = iterator1.next();
            Iterator<IEffect<Object>> iterator2 = next.getEffects().iterator();
            while (iterator2.hasNext()) {
                IEffect<Object> next1 = iterator2.next();
                pendingRemovals.add(next1);
                iterator2.remove();
            }
            iterator1.remove();
        }
    }

    public abstract void stopEffectScheduler();

    public void purgeEffectCache() {
        effectSet.clear();
        pendingAdditions.clear();
        pendingRemovals.clear();
    }


    public Map<IGlobalEffect, EffectParams> parseItemEffects(Map<String, EffectParams> stringEffectParamsMap) {
        Map<IGlobalEffect, EffectParams> map = new HashMap<>();
        for (Map.Entry<String, EffectParams> w : stringEffectParamsMap.entrySet()) {
            IGlobalEffect globalEffect = getGlobalEffect(w.getKey());
            if (globalEffect != null) {
                map.put(globalEffect, w.getValue());
            }
        }
        return map;
    }
}


