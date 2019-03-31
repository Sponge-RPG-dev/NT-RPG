package cz.neumimto.rpg.listeners;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.entities.IEntityType;
import cz.neumimto.rpg.events.entity.IEntityProjectileDamageEvent;
import cz.neumimto.rpg.events.entity.IEntityWeaponDamageEvent;
import cz.neumimto.rpg.events.skill.SkillDamageEvent;
import cz.neumimto.rpg.events.skill.SkillDamageEventLate;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.ProjectileProperties;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

@ResourceLoader.ListenerClass
public class DamageListener {

	@Inject
	private DamageService damageService;

	@Inject
	private EntityService entityService;

	@Inject
	private CauseStackManager causeStackManager;

	@Listener(order = Order.LATE)
	public void onEntityDamage(DamageEntityEvent event, @First EntityDamageSource damageSource) {
		/*if (damageSource.getType() == NDamageType.DAMAGE_CHECK) {
			return;
		}

		 */
		if (damageSource.toString().contains(NDamageType.DAMAGE_CHECK.getName())) {
			return;
		}

		Entity targetEntity = event.getTargetEntity();
		Entity sourceEntity = damageSource.getSource();

		if (damageSource instanceof SkillDamageSource) {
			SkillDamageSource skillDamageSource = (SkillDamageSource) damageSource;
			IEntity caster = skillDamageSource.getSourceIEntity();
			IEntity target = entityService.get(event.getTargetEntity());
			ISkill skill = skillDamageSource.getSkill();
			DamageType type = skillDamageSource.getType();
			IEffect effect = skillDamageSource.getEffect();
			if (caster.getType() == IEntityType.CHARACTER) {
				IActiveCharacter c = (IActiveCharacter) caster;
				if (c.hasPreferedDamageType()) {
					type = c.getDamageType();
				}
			}
			double finalDamage = event.getBaseDamage() * damageService.getEntityDamageMult(caster, type);

			try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
				if (effect != null) {
					causeStackManager.pushCause(effect);
				}
				if (skill != null) {
					causeStackManager.pushCause(skill);
				}

				SkillDamageEvent pre = new SkillDamageEvent(target, skill, finalDamage);
				pre.setCause(causeStackManager.getCurrentCause());
				Sponge.getEventManager().post(pre);
				if (pre.isCancelled() || pre.getDamage() <= 0) {
					event.setCancelled(true);
					return;
				}

				finalDamage = pre.getDamage();
				double target_resistance = damageService.getEntityResistance(target, type);
				finalDamage *= target_resistance;

				SkillDamageEventLate post = new SkillDamageEventLate(target, skill, finalDamage, target_resistance);
				post.setCause(causeStackManager.getCurrentCause());
				Sponge.getEventManager().post(post);
				if (post.isCancelled() || post.getDamage() <= 0) {
					event.setCancelled(true);
					return;
				}

				event.setBaseDamage(post.getDamage());
			}
		} else if (damageSource instanceof IndirectEntityDamageSource) {
			IndirectEntityDamageSource indirectEntityDamageSource = (IndirectEntityDamageSource) damageSource;
			Projectile projectile = (Projectile) indirectEntityDamageSource.getSource();
			if (!(projectile.getShooter() instanceof Entity)) //Projectiles from dispensers, todo?
				return;

			IEntity shooter = entityService.get((Entity) projectile.getShooter());
			IEntity target = entityService.get(event.getTargetEntity());
			ProjectileProperties projectileProperties = ProjectileProperties.cache.get(projectile);
			if (projectileProperties != null) {
				event.setCancelled(true);
				ProjectileProperties.cache.remove(projectile);
				projectileProperties.consumer.accept(event, shooter, target);
				return;
			}

			double projectileDamage = event.getOriginalDamage();
			if (shooter.getType() == IEntityType.CHARACTER) {
				IActiveCharacter c = (IActiveCharacter) shooter;
				projectileDamage = damageService.getCharacterProjectileDamage(c, projectile.getType());
			} else if (shooter.getType() == IEntityType.MOB) {
				if (!pluginConfig.OVERRIDE_MOBS) {
					projectileDamage = entityService.getMobDamage(shooter.getEntity());
				}
			}

			IEntityProjectileDamageEvent hit = new IEntityProjectileDamageEvent(target, projectileDamage, projectile);
			hit.setCause(causeStackManager.getCurrentCause());
			Sponge.getEventManager().post(hit);
			if (hit.isCancelled() || hit.getDamage() <= 0) {
				event.setCancelled(true);
				return;
			}

			event.setBaseDamage(hit.getDamage());
		} else {
			IEntity attacker = entityService.get(sourceEntity);
			double newdamage = event.getBaseDamage();

			if (attacker.getType() == IEntityType.CHARACTER) {
				IActiveCharacter character = (IActiveCharacter) attacker;
				/*
				Hotbar hotbar = character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
                    if (hotbar.getSelectedSlotIndex() != character.getSelectedHotbarSlot()) {
                    character.updateSelectedHotbarSlot();
                    damageService.recalculateCharacterWeaponDamage(character);
                }
                */
				newdamage = character.getWeaponDamage();
			} else {
				if (!pluginConfig.OVERRIDE_MOBS) {
					newdamage = entityService.getMobDamage(sourceEntity);
				}
			}
			newdamage *= damageService.getEntityDamageMult(attacker, damageSource.getType());

			IEntityWeaponDamageEvent e = new IEntityWeaponDamageEvent(entityService.get(targetEntity), newdamage);
			e.setCause(causeStackManager.getCurrentCause());
			Sponge.getEventManager().post(e);
			if (e.isCancelled() || e.getDamage() <= 0) {
				event.setCancelled(true);
				return;
			}

			event.setBaseDamage(e.getDamage());

			//todo defence
			/*
            if (targetEntity.getHotbarObjectType() == EntityTypes.PLAYER) {
                IActiveCharacter tcharacter = characterService.getTarget(targetEntity.getUniqueId());
                double armor = tcharacter.getArmorValue();
                final double damagefactor = damageService.DamageArmorReductionFactor.apply(newdamage, armor);
                event.setBaseDamage(ce.getDamage());
                event.setDamage(DamageModifier.builder().cause(Cause.ofNullable(null)).type(DamageModifierTypes.ARMOR).build(), input -> input
                    * ce.getDamagefactor());
            }*/
		}
	}
}
