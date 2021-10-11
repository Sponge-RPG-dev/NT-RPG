package cz.neumimto.rpg.spigot.listeners;

import com.google.auto.service.AutoService;
import com.google.inject.Singleton;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.events.damage.IEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.damage.AbstractDamageListener;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;

import javax.inject.Inject;
import java.util.Optional;

@Singleton
@AutoService({IRpgListener.class})
@ResourceLoader.ListenerClass
public class SpigotDamageListener extends AbstractDamageListener implements IRpgListener {

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

    @Inject
    private InventoryHandler inventoryHandler;

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
                } else {
                    event.setDamage(0);
                }
                processProjectileDamageEarly(event, attacker, target, projectile);
            }
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
            processWeaponDamageEarly(event, event.getCause(), attacker, target);
        }
    }

    private void processWeaponDamageEarly(EntityDamageByEntityEvent event, EntityDamageEvent.DamageCause cause, IEntity attacker, ISpigotEntity target) {
        double newdamage = event.getDamage();

        RpgItemStack rpgItemStack = null;
        if (attacker.getType() == IEntityType.CHARACTER) {
            ISpigotCharacter character = (ISpigotCharacter) attacker;
            Player player = character.getPlayer();
            PlayerInventory inventory = player.getInventory();

            int last = character.getLastHotbarSlotInteraction();
            int selectedSlotIndex = inventory.getHeldItemSlot();
            if (last != selectedSlotIndex) {
                SpigotInventoryListener.prepareItemInHand(player, character, player.getItemInHand(),
                        selectedSlotIndex, null, EquipmentSlot.HAND, itemService, inventoryHandler);
            }

            if (character.requiresDamageRecalculation()) {
                Player entity = (Player) attacker.getEntity();
                ItemStack itemInMainHand = entity.getInventory().getItemInMainHand();
                RpgItemStack futureMainHand = itemService.getRpgItemStack(itemInMainHand).orElse(null);
                character.setMainHand(futureMainHand, entity.getInventory().getHeldItemSlot());
                RpgItemStack mainHand = character.getMainHand();
                spigotDamageService.recalculateCharacterWeaponDamage(character, mainHand);
                character.setRequiresDamageRecalculation(false);
            }
            newdamage = character.getWeaponDamage();
            rpgItemStack = character.getMainHand();
        } else {
            LivingEntity attackerEntity = (LivingEntity) attacker.getEntity();
            if (!pluginConfig.OVERRIDE_MOBS && entityService.handleMobDamage(attackerEntity.getWorld().getName(), attackerEntity.getUniqueId())) {
                newdamage = entityService.getMobDamage(attackerEntity);
            }
            if (attackerEntity instanceof HumanEntity) {
                ItemStack itemStack = ((HumanEntity) attackerEntity).getItemInHand();

                Optional<RpgItemStack> rpgItemStack1 = itemService.getRpgItemStack(itemStack);
                if (rpgItemStack1.isPresent()) {
                    rpgItemStack = rpgItemStack1.get();
                }

            }
        }
        newdamage *= spigotDamageService.getDamageHandler().getEntityDamageMult(attacker, event.getCause().name());

        IEntityWeaponDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(SpigotEntityWeaponDamageEarlyEvent.class);
        e.setTarget(target);
        e.setDamage(newdamage);
        e.setWeapon(rpgItemStack);

        if (Rpg.get().postEvent(e)) {
            event.setCancelled(true);
            return;
        }

        if (e.getDamage() <= 0) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(e.getDamage());
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

        if (Rpg.get().postEvent(e)) {
            event.setCancelled(true);
            return;
        }

        if (e.getDamage() <= 0) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(e.getDamage());
    }

    private void processProjectileDamageEarly(EntityDamageByEntityEvent event, IEntity attacker, IEntity target, Projectile projectile) {
        double newdamage = event.getDamage();
        if (attacker.getType() == IEntityType.CHARACTER) {
            ISpigotCharacter c = (ISpigotCharacter) attacker;
            newdamage += spigotDamageService.getCharacterProjectileDamage(c, projectile.getType());
        } else if (attacker.getType() == IEntityType.MOB) {
            LivingEntity attackerEntity = (LivingEntity) attacker.getEntity();
            PluginConfig pluginConfig = Rpg.get().getPluginConfig();
            if (!pluginConfig.OVERRIDE_MOBS && entityService.handleMobDamage(attackerEntity.getWorld().getName(), attackerEntity.getUniqueId())) {
                newdamage = entityService.getMobDamage(attackerEntity);
            }
        }

        SpigotEntityProjectileDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(SpigotEntityProjectileDamageEarlyEvent.class);
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

        event.setDamage(e.getDamage());
    }

}
