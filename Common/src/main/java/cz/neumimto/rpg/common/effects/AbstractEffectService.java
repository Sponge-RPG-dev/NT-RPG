package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.*;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.common.assets.AssetService;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;


/**
 * Created by NeumimTo on 17.1.2015.
 */
public abstract class AbstractEffectService implements EffectService {

    public static final long TICK_PERIOD = 5L;
    private static final long unlimited_duration = -1;
    protected Set<IEffect> effectSet = new HashSet<>();
    protected Set<IEffect> pendingAdditions = new HashSet<>();
    protected Set<IEffect> pendingRemovals = new HashSet<>();
    protected Map<String, IGlobalEffect> globalEffects = new HashMap<>();
    @Inject
    private AssetService assetService;
    @Inject
    private SkillService skillService;
    private Map<String, EffectType> effectTypes = new HashMap<>();

    public AbstractEffectService() {
        registerEffectTypes(CommonEffectTypes.class);
        registerEffectTypes(CoreEffectTypes.class);
    }

    @Override
    public void registerEffectType(EffectType effectType) {
        effectTypes.put(effectType.toString().toLowerCase(), effectType);
    }

    @Override
    public void registerEffectTypes(Class<? extends Enum> e) {
        EnumSet.allOf(e).stream().forEach(a -> {
            EffectType type = (EffectType) a;
            registerEffectType(type);
        });
    }

    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
        }
        stopEffect(effect);
    }

    @Override
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

    @Override
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
    @Override
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
    @Override
    public void registerGlobalEffect(IGlobalEffect iGlobalEffect) {
        globalEffects.put(iGlobalEffect.getName().toLowerCase(), iGlobalEffect);
    }

    /**
     * Removes cached globaleffect
     *
     * @param name
     */
    @Override
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
    @Override
    public IGlobalEffect getGlobalEffect(String name) {
        return globalEffects.get(name.toLowerCase());
    }

    @Override
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
    @Override
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
    @Override
    public void applyGlobalEffectsAsEnchantments(Map<IGlobalEffect, EffectParams> map, IEffectConsumer consumer,
                                                 IEffectSourceProvider effectSourceType) {
        map.forEach((e, l) ->
                applyGlobalEffectAsEnchantment(e, consumer, l, effectSourceType)
        );
    }

    @Override
    public void removeGlobalEffectsAsEnchantments(Collection<IGlobalEffect> itemEffects, IActiveCharacter character,
                                                  IEffectSourceProvider effectSourceProvider) {
        if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
            character.sendMessage(itemEffects.size() + " added echn. effect to remove queue.");
        }
        itemEffects.forEach((e) -> {
            removeEffect(e.getName(), character, effectSourceProvider);
        });
    }

    @Override
    public boolean isGlobalEffect(String s) {
        return globalEffects.containsKey(s.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    /**
     * Called only in cases when entities dies, or players logs off
     */
    @Override
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

    @Override
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

    @Override
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

    @Override
    public void load() {
        File file = new File(Rpg.get().getWorkingDirectory(), "SkillsAndEffects.md");
        if (file.exists()) {
            file.delete();
        }

        try {
            StringBuilder finalString = new StringBuilder();
            file.createNewFile();
            String template = assetService.getAssetAsString("templates/Effect.md");
            for (Map.Entry<String, IGlobalEffect> effect : globalEffects.entrySet()) {
                Class aClass = effect.getValue().asEffectClass();
                if (aClass != null && aClass.isAnnotationPresent(Generate.class)) {
                    Generate meta = (Generate) aClass.getAnnotation(Generate.class);
                    String description = meta.description();
                    String name = effect.getKey();

                    Class<?> modelType = EffectModelFactory.getModelType(aClass);

                    String s = new String(template);
                    s = s.replaceAll("\\{\\{effect\\.name}}", name);
                    s = s.replaceAll("\\{\\{effect\\.description}}", description);

                    if (EffectModelFactory.getTypeMappers().containsKey(modelType)) {
                        s = s.replaceAll("\\{\\{effect\\.parameter}}", modelType.getSimpleName());
                        s = s.replaceAll("\\{\\{effect\\.parameters}}", "");
                    } else if (modelType == null) {
                        s = s.replaceAll("\\{\\{effect\\.parameter}}", "");
                        s = s.replaceAll("\\{\\{effect\\.parameters}}", "");
                    } else {
                        Field[] fields = modelType.getFields();
                        s = s.replaceAll("\\{\\{effect\\.parameter}}", "");
                        StringBuilder buffer = new StringBuilder();
                        for (Field field : fields) {
                            String fname = field.getName();
                            String type = field.getType().getSimpleName();
                            buffer.append("   * ").append(fname).append(" - ").append(type).append("\n\n");
                        }
                        s = s.replaceAll("\\{\\{effect\\.parameters}}", buffer.toString());
                    }
                    finalString.append(s);
                }
            }

            template = assetService.getAssetAsString("templates/Skill.md");
            StringBuilder skills = new StringBuilder();
            for (ISkill iSkill : skillService.getAll()) {

                String damageType = iSkill.getDamageType();
                String s = new String(template);
                s = s.replaceAll("\\{\\{skill\\.damageType}}", damageType == null ? "Deals no damage" : damageType);

                String id = iSkill.getId();
                s = s.replaceAll("\\{\\{skill\\.id}}", id);

                SkillSettings defaultSkillSettings = iSkill.getDefaultSkillSettings();

                StringBuilder buffer = new StringBuilder();
                for (Map.Entry<String, String> stringFloatEntry : defaultSkillSettings.getNodes().entrySet()) {
                    buffer.append("   * ").append(stringFloatEntry.getKey()).append("\n\n");
                }
                s = s.replaceAll("\\{\\{skill\\.parameters}}", buffer.toString());
                skills.append(s);
            }
            template = assetService.getAssetAsString("templates/SE.md");

            Files.write(file.toPath(), template.replaceAll("\\{\\{effects}}", finalString.toString())
                    .replaceAll("\\{\\{skills}}", skills.toString()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


