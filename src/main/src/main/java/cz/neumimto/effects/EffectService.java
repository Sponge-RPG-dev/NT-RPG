package cz.neumimto.effects;


import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
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


    public void runEffect(IEffect effect) {
        effect.onApply();
        if (effect.requiresRegister())
            pendingAdditions.add(effect);
    }

    public void stopEffect(IEffect effect) {
        effect.onRemove();
        purgeEffect(effect);
    }

    public void purgeEffect(IEffect effect) {
        if (effect.requiresRegister())
            pendingRemovals.remove(effect);
    }

    @PostProcess(priority = 6)
    public void run() {
        game.getScheduler().createTaskBuilder().name("EffectTask")
                .delay(10L).interval(TICK_PERIOD, TimeUnit.MILLISECONDS)
                .execute(() -> {
                    pendingRemovals.stream().forEach(e -> {
                        if (effectSet.contains(e)) {
                            effectSet.remove(e);
                        }
                    });
                    pendingRemovals.clear();
                    pendingAdditions.stream().forEach(e -> {
                        if (effectSet.contains(e))
                            stackEffect(e);
                        else
                            effectSet.add(e);
                    });
                    pendingAdditions.clear();
                    final long serverTime = System.currentTimeMillis();
                    effectSet.stream().forEach(e -> {
                        if (e.getExpireTime() <= serverTime) {
                            stopEffect(e);
                        }
                        if (e.getPeriod() + e.getLastTickTime() <= serverTime) {
                            tickEffect(e);
                        }
                    });
                }).submit(plugin);
    }

    public void tickEffect(IEffect effect) {
        effect.onTick();
        effect.tickCountIncrement();
    }

    public void stackEffect(IEffect effect) {
        effect.onStack(effect.getLevel());
        effect.setLevel(effect.getLevel() + 1);
    }

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

    public void removeEffect(IEffect iEffect, IEffectConsumer consumer) {
        IEffect effect = consumer.getEffect(iEffect.getClass());
        if (effect != null) {
            consumer.removeEffect(iEffect.getClass());
            stopEffect(effect);
        }
    }


    public void registerGlobalEffect(IGlobalEffect iGlobalEffect) {
        globalEffects.put(iGlobalEffect.getName().toLowerCase(), iGlobalEffect);
    }

    public void removeGlobalEffect(String name) {
        name = name.toLowerCase();
        if (globalEffects.containsKey(name))
            globalEffects.remove(name);
    }

    public IGlobalEffect getGlobalEffect(String name) {
        return globalEffects.get(name.toLowerCase());
    }

    public void applyGlobalEffect(String effectname, IEffectConsumer consumer, long duration, int level) {
        IGlobalEffect g = globalEffects.get(effectname);
        if (g != null) {
            IEffect a = g.construct(consumer, duration, level);
            addEffect(a,consumer);
        }
    }

    public void applyGlobalEffectAsEnchantment(String effectname, IEffectConsumer consumer, int level) {
        applyGlobalEffect(effectname, consumer, enchantment_duration, level);
    }

}


