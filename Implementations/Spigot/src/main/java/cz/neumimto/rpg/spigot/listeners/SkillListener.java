package cz.neumimto.rpg.spigot.listeners;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectType;
import cz.neumimto.rpg.spigot.effects.common.Rage;
import cz.neumimto.rpg.spigot.events.character.SpigotEffectApplyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Singleton;

@ResourceLoader.ListenerClass
@Singleton
public class SkillListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEffectApply(SpigotEffectApplyEvent event) {
        if (event.getEffect().getConsumer().hasEffect(Rage.name)) {
            for (EffectType removeType : Rage.removeTypes) {
                if (event.getEffect().getEffectTypes().contains(removeType)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}
