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

package cz.neumimto.rpg.effects;


import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by NeumimTo on 17.1.2015.
 */
@Singleton
public class EffectService {

    public static final long TICK_PERIOD = 250L;
    private static final long unlimited_duration = -1;
    @Inject
    private Game game;
    @Inject
    private NtRpgPlugin plugin;
    private Set<IEffect> effectSet = new HashSet<>();
    private Set<IEffect> pendingAdditions = new HashSet<>();
    private Set<IEffect> pendingRemovals = new HashSet<>();
    private Map<String, IGlobalEffect> globalEffects = new HashMap<>();

    /**
     * calls effect.onApply and registers if effect requires
     *
     * @param effect
     */
    public void runEffect(IEffect effect) {
        pendingAdditions.add(effect);
    }

    /**
     * Stops the effect and calls onRemove
     *
     * @param effect
     */
    public void stopEffect(IEffect effect) {
        effect.onRemove();
        if (effect.requiresRegister()) {
            pendingRemovals.add(effect);
        }
    }


    /**
     * Attempts to remove all references of given object from the scheduler
     * Wont call onRemove
     *
     * @param effect
     */
    public void purgeEffect(IEffect effect) {
        if (effect.requiresRegister()) {
            try {
                pendingRemovals.remove(effect);
                pendingAdditions.remove(effect);
                effectSet.remove(effect);
                IEffectConsumer consumer = effect.getConsumer();
                if (consumer != null) {
                    consumer.removeEffect(effect);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @PostProcess(priority = 1000)
    public void run() {
        game.getScheduler().createTaskBuilder().name("EffectTask")
                .delay(10L, TimeUnit.MILLISECONDS).interval(TICK_PERIOD, TimeUnit.MILLISECONDS)
                .execute(() -> {
                    for (IEffect pendingRemoval : pendingRemovals) {
                        if (effectSet.contains(pendingRemoval)) {
                            effectSet.remove(pendingRemoval);
                            IEffectConsumer consumer = pendingRemoval.getConsumer();
                            if (consumer != null) {
                                consumer.removeEffect(pendingRemoval);
                            }
                        }
                    }
                    pendingRemovals.clear();
                    long l = System.currentTimeMillis();
                    for (IEffect e : effectSet) {

                        if (e.getPeriod() + e.getLastTickTime() <= l) {
                            tickEffect(e, l);
                        }

                        if (e.getDuration() == unlimited_duration) {
                            continue;
                        }

                        if (e.getExpireTime() <= l) {
                            stopEffect(e);
                        }
                    }

                    for (IEffect pendingAddition : pendingAdditions) {
                        effectSet.add(pendingAddition);
                    }
                    pendingAdditions.clear();
                }).submit(plugin);
    }

    /**
     * Calls onTick and increments tickCount
     *
     * @param effect
     */
    public void tickEffect(IEffect effect, long time) {
        effect.onTick();
        effect.tickCountIncrement();
        effect.setLastTickTime(time);
    }

    /**
     * Stacks effect and inceremnt effect level by 1
     *
     * @param effect
     */
    public void stackEffect(IEffect effect, IEffectSourceProvider provider) {
        effect.setStacks(effect.getStacks() + 1);
        effect.onStack(effect, provider);
    }

    /**
     * Adds effect to the consumer,
     * Effects requiring register are registered into the scheduler
     * If the consumer already has same effect the effect is stacked
     * If the effect is not stackable and level of new effect is greater than level of old effect, the old effect is replaced by new one, but wont call onApply
     *
     * @param iEffect
     * @param consumer
     */
    public void addEffect(IEffect iEffect, IEffectConsumer consumer, IEffectSourceProvider effectSourceProvider) {
        IEffect eff = consumer.getEffect(iEffect.getClass().getName());
        if (eff == null) {
            consumer.addEffect(iEffect);
            iEffect.onApply();
            if (iEffect.requiresRegister())
                runEffect(iEffect);
        } else if (eff.isStackable()) {
            stackEffect(iEffect, effectSourceProvider);
        } else {
            if (eff.getStacks() >= iEffect.getStacks()) {
                if (iEffect.requiresRegister()) {
                    consumer.removeEffect(eff.getClass().getName());
                }
                consumer.addEffect(iEffect);
            }
        }
    }

    /**
     * Removes and stops the effect
     *
     * @param iEffect
     * @param consumer
     */
    public void removeEffect(IEffect iEffect, IEffectConsumer consumer) {
        removeEffect(iEffect, consumer);
    }

    /**
     * Removes and stops the effect
     *
     * @param iEffect
     * @param consumer
     */
    public void removeEffect(String iEffect, IEffectConsumer consumer) {
        IEffect effect = consumer.getEffect(iEffect);
        if (effect != null) {
            consumer.removeEffect(iEffect);
            stopEffect(effect);
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
        if (globalEffects.containsKey(name))
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

    /**
     * Applies global effect with unlimited duration
     *
     * @param effect
     * @param consumer
     * @param value
     */
    public void applyGlobalEffectAsEnchantment(IGlobalEffect effect, IEffectConsumer consumer, String value, IEffectSourceProvider effectSourceType) {
        IEffect construct = effect.construct(consumer, unlimited_duration, value);
        addEffect(construct, consumer, effectSourceType);
    }

    /**
     * Applies global effects with unlimited duration
     *
     * @param map
     * @param consumer
     */
    public void applyGlobalEffectsAsEnchantments(Map<IGlobalEffect, String> map, IEffectConsumer consumer, IEffectSourceProvider effectSourceType) {
        map.forEach((e, l) ->
            applyGlobalEffectAsEnchantment(e, consumer, l, effectSourceType)
        );
    }


    public void removeGlobalEffectsAsEnchantments(Map<IGlobalEffect, String> itemEffects, IActiveCharacter character, IEffectSourceProvider effectSourceProvider) {
        itemEffects.forEach((e, l) -> {
            IEffect effect = character.getEffect(e.getName());
            if (effect.getStacks() - 1 <= 0) {
                character.removeEffect(e.getName());
            } else {
                effect.setStacks(effect.getStacks() + 1);
                effect.onStack(effect, effectSourceProvider);
            }
        });
    }

    public boolean isGlobalEffect(String s) {
        return globalEffects.containsKey(s.toLowerCase());
    }

    public void removeAllEffects(IActiveCharacter character) {
        pendingRemovals.addAll(character.getEffects());
    }
}


