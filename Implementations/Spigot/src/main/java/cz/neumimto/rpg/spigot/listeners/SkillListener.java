package cz.neumimto.rpg.spigot.listeners;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectType;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.api.events.damage.DamageIEntityEarlyEvent;
import cz.neumimto.rpg.spigot.effects.SpigotEffectService;
import cz.neumimto.rpg.spigot.effects.common.ManaShieldEffect;
import cz.neumimto.rpg.spigot.effects.common.Rage;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.events.character.SpigotEffectApplyEvent;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntityProjectileDamageEarlyEvent;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntitySkillDamageEarlyEvent;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.spigot.services.IRpgListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(IRpgListener.class)
@ResourceLoader.ListenerClass
public class SkillListener implements IRpgListener {

    @Inject
    private SpigotEffectService effectService;

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamageProjectileEarly(SpigotEntityProjectileDamageEarlyEvent event) {
        processManaEffect(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamageSkillEarly(SpigotEntitySkillDamageEarlyEvent event) {
        processManaEffect(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamageWeaponEarly(SpigotEntityWeaponDamageEarlyEvent event) {
        processManaEffect(event);
    }

    protected void processManaEffect(DamageIEntityEarlyEvent event) {
        ManaShieldEffect manaShieldEffect = (ManaShieldEffect) event.getTarget().getEffect(ManaShieldEffect.name);
        if (manaShieldEffect != null) {
            double multiplier = manaShieldEffect.getMultiplier();
            double newDamage = event.getDamage() * multiplier;
            double manaDamage = event.getDamage() - newDamage;
            if (newDamage == 0) {
                event.setCancelled(true);
            } else {
                event.setDamage(newDamage);
                if (event.getTarget() instanceof ISpigotCharacter) {
                    ISpigotCharacter character = (ISpigotCharacter) event.getTarget();
                    IReservable mana = character.getMana();
                    double newMana = mana.getValue() - manaDamage;
                    if (newMana <= 0) {
                        newMana = 0;
                        effectService.removeEffect(manaShieldEffect, event.getTarget());
                    }
                    character.getMana().setValue(newMana);
                }
            }
        }
    }

}
