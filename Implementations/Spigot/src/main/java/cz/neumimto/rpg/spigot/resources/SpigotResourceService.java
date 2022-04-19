package cz.neumimto.rpg.spigot.resources;

import com.google.inject.Singleton;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.resources.UiResourceTracker;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SpigotResourceService extends ResourceService {


    public SpigotResourceService() {
        super();
        guiRegistry.put(mana, new UiResourceTracker(true) {
            @Override
            public IEffect getOrCreateEffect(IActiveCharacter character, Resource resource) {

                effect = Rpg.get().getPluginConfig().RESOURCE_UI_DISPLAY_TYPE.equals("BOSSBAR") ? new ManaBarBossBar(character) : new ManaBarText(character);

                Rpg.get().getPluginConfig().
                        IEffectContainer<Object, ManaBarBossBar> barExpNotifier = character.getEffect(ManaBarBossBar.name);
                ManaBar effect = (ManaBar) barExpNotifier;
                if (effect == null) {
                    effectService.addEffect(effect.asEffect(), InternalEffectSourceProvider.INSTANCE);
                }
                effect.notifyManaChange();
                return effect;
            }
        });

        guiRegistry.put(rage, new UiResourceTracker(true) {{

        }});
    }

    @Override
    protected Resource getHpTracker(IActiveCharacter character) {
        return new Health(character.getUUID());
    }

    @Override
    protected Resource getStaminaTracker(IActiveCharacter character) {
        return null;
    }

    @Override
    public Resource initializeForAi(AbstractMob mob) {
        return new MobHealth(mob.getUUID());
    }

    @Override
    public void notifyChange(IActiveCharacter character, Resource resource) {
        UiResourceTracker uiResourceTracker = guiRegistry.get(resource.getType());
        if (uiResourceTracker == null) {
            return;
        }
        if (uiResourceTracker.isEffectBased) {
            IEffect effect = uiResourceTracker.getOrCreateEffect(character, resource);
            effect.onTick(effect);
        } else {
            uiResourceTracker.runCustomTask(character, resource);
        }
    }
}
