package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.SkillService;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;


/**
 * Created by NeumimTo on 17.1.2015.
 */
public abstract class EffectService {

    public static final long TICK_PERIOD = 5L;
    protected static final long unlimited_duration = -1;
    protected Set<IEffect> effectSet = new HashSet<>();
    protected Set<IEffect> pendingAdditions = new HashSet<>();
    protected Set<IEffect> pendingRemovals = new HashSet<>();
    protected Map<String, IGlobalEffect> globalEffects = new HashMap<>();
    private Map<String, EffectType> effectTypes = new HashMap<>();

    public EffectService() {
        registerEffectTypes(CommonEffectTypes.class);
        registerEffectTypes(CoreEffectTypes.class);
    }

    public void registerEffectType(EffectType effectType) {
        effectTypes.put(effectType.toString().toLowerCase(), effectType);
    }

    public void registerEffectTypes(Class<? extends Enum> e) {
        EnumSet.allOf(e).stream().forEach(a -> {
            EffectType type = (EffectType) a;
            registerEffectType(type);
        });
    }

    public Optional<EffectType> getEffectType(String effectType) {
        return Optional.ofNullable(effectTypes.get(effectType.toLowerCase()));
    }

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

    protected boolean mayTick(IEffect e) {
        return !e.getConsumer().isDetached();
    }

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
            if (consumer1 instanceof ActiveCharacter) {
                ActiveCharacter chara = (ActiveCharacter) consumer1;
                chara.sendMessage("Adding effect: " + effect.getName() +
                        " container: " + (eff == null ? "null" : eff.getEffects().size()) +
                        " provider: " + effectSourceProvider.getType().getClass().getSimpleName());
            }
        }
        if (eff == null) {
            eff = effect.constructEffectContainer();
            effect.getConsumer().addEffect(eff);
            eff.getEffects().add(effect);
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
            if (consumer1 instanceof ActiveCharacter) {
                ActiveCharacter chara = (ActiveCharacter) consumer1;
                chara.sendMessage("Removing effect: " + effect.getName() +
                        " container: " + (container == null ? "null" : container.getEffects().size()));
            }
        }
        if (container != null) {
            removeEffectContainer(container, effect, consumer);
        }
        stopEffect(effect);
    }

    public int removeEffectsByType(IEffectConsumer consumer, Set<EffectType> type) {
        Map<String, IEffectContainer<Object, IEffect<Object>>> map = consumer.getEffectMap();
        int i = 0;
        for (Map.Entry<String, IEffectContainer<Object, IEffect<Object>>> m : map.entrySet()) {
            IEffectContainer<Object, IEffect<Object>> value = m.getValue();
            Set<IEffect<Object>> effects = value.getEffects();
            Set<EffectType> set = new HashSet<>();
            for (IEffect<Object> effect : effects) {
                set.addAll(type);
                set.addAll(effect.getEffectTypes());
                if (set.size() != effect.getEffectTypes().size()) {
                    stopEffect(effect);
                    i++;
                }
                set.clear();
            }
        }
        return i;
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
        }
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

    public void removeGlobalEffectsAsEnchantments(Collection<IGlobalEffect> itemEffects, ActiveCharacter character,
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

    public void purgeEffectCache() {
        for (IEffect iEffect : effectSet) {
            try {
                iEffect.onRemove(iEffect);
            } catch (Throwable t) {
            }
        }
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

    public void load() {
        File file = new File(Rpg.get().getWorkingDirectory(), "SkillsAndEffects.md");
        if (file.exists()) {
            file.delete();
        }

    }

    public abstract void startEffectScheduler();

    public abstract void stopEffectScheduler();
}


