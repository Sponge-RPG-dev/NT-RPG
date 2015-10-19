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

package cz.neumimto.effects;


import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.Game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 17.1.2015.
 */
@Singleton
public class EffectService {

    @Inject
    private Game game;

    @Inject
    private NtRpgPlugin plugin;

    public static final long TICK_PERIOD = 100L;
    private Set<IEffect> effectSet = new HashSet<>();
    private Set<IEffect> pendingAdditions = new HashSet<>();
    private Set<IEffect> pendingRemovals = new HashSet<>();
    private static final long enchantment_duration = -1;
    private Map<String, IGlobalEffect> globalEffects = new HashMap<>();

    /**
     * calls effect.onApply and registers if effect requires
     *
     * @param effect
     */
    public void runEffect(IEffect effect) {
        effect.onApply();
        if (effect.requiresRegister())
            pendingAdditions.add(effect);
    }

    /**
     * Stops the effect and calls onRemove
     *
     * @param effect
     */
    public void stopEffect(IEffect effect) {
        effect.onRemove();
        if (effect.requiresRegister())
            pendingRemovals.remove(effect);
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //just for better readability
    private Consumer<IEffect> removeExisting = (e) -> {
        if (effectSet.contains(e)) effectSet.remove(e);
    };
    private Consumer<IEffect> addPendings = (e) -> {
        if (effectSet.contains(e)) {
            stackEffect(e);
        } else {
            effectSet.add(e);
        }
    };
    private Consumer<IEffect> effectTick = (e) -> {
        final long serverTime = System.currentTimeMillis();
        if (e.getExpireTime() <= serverTime) {
            stopEffect(e);
        }
        if (e.getPeriod() + e.getLastTickTime() <= serverTime) {
            tickEffect(e);
        }
    };

    //Lets assume that average mc server has 50-60, branch prediction is not worth esp with java
    @PostProcess(priority = 1000)
    public void run() {
        game.getScheduler().createTaskBuilder().name("EffectTask")
                .delay(10L, TimeUnit.MILLISECONDS).interval(TICK_PERIOD, TimeUnit.MILLISECONDS)
                .execute(() -> {
                    pendingRemovals.stream().forEach(removeExisting);
                    pendingRemovals.clear();
                    pendingAdditions.stream().forEach(addPendings);
                    pendingAdditions.clear();
                    effectSet.stream().forEach(effectTick);
                }).submit(plugin);
    }

    /**
     * Calls onTick and increments tickCount
     *
     * @param effect
     */
    public void tickEffect(IEffect effect) {
        effect.onTick();
        effect.tickCountIncrement();
    }

    /**
     * Stacks effect and inceremnt effect level by 1
     *
     * @param effect
     */
    public void stackEffect(IEffect effect) {
        effect.setLevel(effect.getLevel() + 1);
        effect.onStack(effect.getLevel());
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
    public void addEffect(IEffect iEffect, IEffectConsumer consumer) {
        IEffect eff = consumer.getEffect(iEffect.getClass());
        if (eff == null) {
            consumer.addEffect(iEffect);
            if (iEffect.requiresRegister())
                runEffect(iEffect);
        } else if (eff.isStackable()) {
            stackEffect(iEffect);
        } else {
            if (eff.getLevel() >= iEffect.getLevel()) {
                if (iEffect.requiresRegister()) {
                    consumer.removeEffect(eff.getClass());
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
        removeEffect(iEffect.getClass(), consumer);
    }

    /**
     * Removes and stops the effect
     *
     * @param iEffect
     * @param consumer
     */
    public void removeEffect(Class<? extends IEffect> iEffect, IEffectConsumer consumer) {
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
     * @param level
     */
    public void applyGlobalEffectAsEnchantment(IGlobalEffect effect, IEffectConsumer consumer, int level) {
        IEffect construct = effect.construct(consumer, enchantment_duration, level);
        addEffect(construct, consumer);
    }

    /**
     * Applies global effects with unlimited duration
     *
     * @param map
     * @param consumer
     */
    public void applyGlobalEffectsAsEnchantments(Map<IGlobalEffect, Integer> map, IEffectConsumer consumer) {
        map.forEach((e, l) -> {
            applyGlobalEffectAsEnchantment(e, consumer, l);
        });
    }


    public void removeGlobalEffectsAsEnchantments(Map<IGlobalEffect, Integer> itemEffects, IActiveCharacter character) {
        itemEffects.forEach((e,l) -> {
            IEffect effect = character.getEffect(e.asEffectClass());
            if (effect.getLevel() - l <= 0) {
                character.removeEffect(e.asEffectClass());
            } else {
                effect.setLevel(effect.getLevel() - l);
                effect.onStack(effect.getLevel());
            }
        });
    }
}


