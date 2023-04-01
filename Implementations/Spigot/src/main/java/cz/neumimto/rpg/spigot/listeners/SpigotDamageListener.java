package cz.neumimto.rpg.spigot.listeners;

import com.google.auto.service.AutoService;
import com.google.inject.Singleton;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.events.damage.IEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.effects.common.DefaultRageDecay;
import cz.neumimto.rpg.spigot.effects.common.model.DefaultRageDecayModel;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntityProjectileDamageEarlyEvent;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntitySkillDamageEarlyEvent;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import cz.neumimto.rpg.spigot.services.IRpgListener;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.projectiles.ProjectileSource;

import javax.inject.Inject;
import java.util.Optional;

@Singleton
@AutoService({IRpgListener.class})
@ResourceLoader.ListenerClass
public class SpigotDamageListener implements IRpgListener {

    @Inject
    private SpigotEntityService entityService;

    @Inject
    private SpigotDamageService spigotDamageService;

    @Inject
    private SpigotItemService itemService;

    @Inject
    private PluginConfig pluginConfig;

    @Inject
    private SpigotRpg spigotRpg;

    @EventHandler
    public void projectileHitBlockEvent(ProjectileHitEvent event) {
        Block hitBlock = event.getHitBlock();
        if (hitBlock != null) {
            ProjectileCache projectileCache = ProjectileCache.cache.get(event.getEntity());
            if (projectileCache != null) {
                ProjectileCache.cache.remove(event.getEntity());
                projectileCache.process(hitBlock);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEarly(EntityDamageByEntityEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getEntity())) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }
        Entity targetEntity = event.getEntity();
        Entity attackerEntity = event.getDamager();

        if (!(targetEntity instanceof LivingEntity)) {
            return;
        }

        LivingEntity living = (LivingEntity) targetEntity;
        ISpigotEntity target = (ISpigotEntity) entityService.get(living);

        IEntity attacker = null;
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile) attackerEntity;
            ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof LivingEntity) {
                attacker = entityService.get((LivingEntity) shooter);

                ProjectileCache projectileProperties = ProjectileCache.cache.get(projectile);
                if (projectileProperties != null) {
                    ProjectileCache.cache.remove(projectile);
                    projectileProperties.consumer.accept(event, (ISpigotEntity) attacker, target);
                    PlayerSkillContext skill = projectileProperties.getSkill();
                    if (skill != null) {
                        target.setSkillOrEffectDamageCause(skill.getSkill());
                    }
                    return;
                }
                processProjectileDamageEarly(event, attacker, target, projectile);
            }
            return;
        } else {
            if (!(attackerEntity instanceof LivingEntity)) {
                return;
            }
            attacker = entityService.get((LivingEntity) attackerEntity);
        }

        if (target.skillOrEffectDamageCause() != null) {
            processSkillDamageEarly(event, target.skillOrEffectDamageCause(), attacker, target);
            target.setSkillOrEffectDamageCause(null);
        } else {
            if (attackerEntity instanceof HumanEntity ae) {
                IEntity iEntity = entityService.get(ae);
                if (iEntity.getType() == IEntityType.CHARACTER) {
                    Optional<RpgItemStack> rpgItemStack = itemService.getRpgItemStack(ae.getInventory().getItemInMainHand());
                    if (rpgItemStack.isPresent()) {
                        if (!itemService.checkItemPermission((ActiveCharacter) iEntity, rpgItemStack.get(), EquipmentSlot.HAND.name())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                processWeaponDamageEarly(event, event.getCause(), attacker, target);
            }
        }
    }

    private void processWeaponDamageEarly(EntityDamageByEntityEvent event, EntityDamageEvent.DamageCause cause, IEntity attacker, ISpigotEntity target) {
        double newdamage = event.getDamage();

        RpgItemStack rpgItemStack = null;

        newdamage *= spigotDamageService.getDamageHandler().getEntityDamageMult(attacker, event.getCause().name());

        IEntityWeaponDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(SpigotEntityWeaponDamageEarlyEvent.class);
        e.setTarget(target);
        e.setDamage(newdamage);
        e.setWeapon(rpgItemStack);
        e.setDamager(attacker);

        if (Rpg.get().postEvent(e)) {
            event.setCancelled(true);
            return;
        }

        if (e.getDamage() <= 0) {
            event.setCancelled(true);
            return;
        }
        event.setDamage(e.getDamage());

        processRageGain((IEntity) e.getTarget(), e.getDamager());
    }

    public static void processRageGain(IEntity target, IEntity damager) {
        if (target.hasEffect(DefaultRageDecay.name) && target instanceof ActiveCharacter a) {
            Resource resource = a.getResource(ResourceService.rage);
            if (resource.getMaxValue() == 0) {
                return;
            }

            DefaultRageDecay effect = (DefaultRageDecay) target.getEffect(DefaultRageDecay.name);
            DefaultRageDecayModel value = effect.getValue();
            if (damager.getType() == IEntityType.CHARACTER) {
                Rpg.get().getCharacterService().gainResource(a, value.damage_taken_from_players, effect, ResourceService.rage);
            } else {
                Rpg.get().getCharacterService().gainResource(a, value.damage_taken_from_mobs, effect, ResourceService.rage);
            }
        }
        if (damager.hasEffect(DefaultRageDecay.name) && damager instanceof ActiveCharacter a) {
            Resource resource = a.getResource(ResourceService.rage);
            if (resource.getMaxValue() == 0) {
                return;
            }

            DefaultRageDecay effect = (DefaultRageDecay) target.getEffect(DefaultRageDecay.name);
            DefaultRageDecayModel value = effect.getValue();
            if (damager.getType() == IEntityType.CHARACTER) {
                Rpg.get().getCharacterService().gainResource(a, value.damage_dealt_to_players, effect, ResourceService.rage);
            } else {
                Rpg.get().getCharacterService().gainResource(a, value.damage_dealt_to_mobs, effect, ResourceService.rage);
            }
        }
    }

    private void processSkillDamageEarly(EntityDamageByEntityEvent event, ISkill skill, IEntity attacker, ISpigotEntity target) {

        EntityDamageEvent.DamageCause type = event.getCause();

        if (attacker.getType() == IEntityType.CHARACTER) {
            SpigotCharacter c = (SpigotCharacter) attacker;
            if (c.hasPreferedDamageType()) {
                type = spigotDamageService.damageTypeById(c.getDamageType());
            }
        }
        double newdamage = event.getDamage() * spigotDamageService.getDamageHandler().getEntityDamageMult(attacker, type.name());

        SpigotEntitySkillDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(SpigotEntitySkillDamageEarlyEvent.class);
        e.setTarget(target);
        e.setDamage(newdamage);
        e.setSkill(skill);
        e.setDamager(attacker);

        if (Rpg.get().postEvent(e)) {
            event.setCancelled(true);
            return;
        }

        if (e.getDamage() <= 0) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(e.getDamage());

        processRageGain(target, attacker);
    }

    private void processProjectileDamageEarly(EntityDamageByEntityEvent event, IEntity attacker, IEntity target, Projectile projectile) {
        double newdamage = event.getDamage();

        SpigotEntityProjectileDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(SpigotEntityProjectileDamageEarlyEvent.class);
        e.setDamager(attacker);
        e.setTarget(target);
        e.setDamage(newdamage);
        e.setProjectile(projectile);

        if (Rpg.get().postEvent(e)) {
            event.setCancelled(true);
            return;
        }

        if (e.getDamage() <= 0) {
            event.setCancelled(true);
            return;
        }
        processRageGain(attacker, e.getTarget());

        event.setDamage(e.getDamage());
    }

}
