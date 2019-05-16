package cz.neumimto.rpg.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SpongeDamageService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.entities.IEntityType;
import cz.neumimto.rpg.events.damage.*;
import cz.neumimto.rpg.inventory.SpongeItemService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.ProjectileProperties;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

import javax.inject.Inject;
import java.util.Optional;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

@Singleton
@ResourceLoader.ListenerClass
public class DamageListener {

	@Inject
	private SpongeDamageService spongeDamageService;

	@Inject
	private EntityService entityService;

	@Inject
	private SpongeItemService itemService;

	@Inject
	private CauseStackManager causeStackManager;

	@Listener(order = Order.EARLY, beforeModifications = true)
	public void onEntityDamageEarly(DamageEntityEvent event, @First EntityDamageSource damageSource) {
		if (damageSource.getType() == NDamageType.DAMAGE_CHECK) {
			return;
		}

		IEntity target = entityService.get(event.getTargetEntity());
		IEntity attacker;
		if (damageSource instanceof IndirectEntityDamageSource) {
			attacker = entityService.get(((IndirectEntityDamageSource) damageSource).getIndirectSource());
		} else attacker = entityService.get(damageSource.getSource());

		if (damageSource instanceof SkillDamageSource) {
			processSkillDamageEarly(event, (SkillDamageSource) damageSource, attacker, target);
		} else if (damageSource instanceof IndirectEntityDamageSource) {
			IndirectEntityDamageSource indirectEntityDamageSource = (IndirectEntityDamageSource) damageSource;
			Projectile projectile = (Projectile) indirectEntityDamageSource.getSource();
			ProjectileProperties projectileProperties = ProjectileProperties.cache.get(projectile);
			if (projectileProperties != null) {
				event.setCancelled(true);
				ProjectileProperties.cache.remove(projectile);
				projectileProperties.consumer.accept(event, attacker, target);
				return;
			}

			processProjectileDamageEarly(event, indirectEntityDamageSource, attacker, target, projectile);
		} else {
			processWeaponDamageEarly(event, damageSource, attacker, target);
		}
	}

	@Listener(order = Order.LATE)
	public void onEntityDamageLate(DamageEntityEvent event, @First EntityDamageSource damageSource) {
		if (damageSource.getType() == NDamageType.DAMAGE_CHECK) {
			return;
		}

		IEntity target = entityService.get(event.getTargetEntity());
		IEntity attacker;
		if (damageSource instanceof IndirectEntityDamageSource) {
			attacker = entityService.get(((IndirectEntityDamageSource) damageSource).getIndirectSource());
		} else attacker = entityService.get(damageSource.getSource());

		if (damageSource instanceof SkillDamageSource) {
			processSkillDamageLate(event, (SkillDamageSource) damageSource, attacker, target);
		} else if (damageSource instanceof IndirectEntityDamageSource) {
			processProjectileDamageLate(event, (IndirectEntityDamageSource) damageSource, attacker, target, (Projectile) damageSource.getSource());
		} else {
			processWeaponDamageLate(event, damageSource, target);
		}
	}

	private void processWeaponDamageEarly(DamageEntityEvent event, EntityDamageSource source, IEntity attacker, IEntity target) {
		double newdamage = event.getBaseDamage();

		RpgItemStack rpgItemStack = null;
		if (attacker.getType() == IEntityType.CHARACTER) {
			IActiveCharacter character = (IActiveCharacter) attacker;
			if (character.requiresDamageRecalculation()) {
				RpgItemStack mainHand = character.getMainHand();
				spongeDamageService.recalculateCharacterWeaponDamage(character, mainHand);
				character.setRequiresDamageRecalculation(false);
			}
			newdamage = character.getWeaponDamage();
			rpgItemStack = character.getMainHand();
		} else {
			Living entity = attacker.getEntity();
			if (!pluginConfig.OVERRIDE_MOBS) {
				newdamage = entityService.getMobDamage(entity);
			}
			if (entity instanceof ArmorEquipable) {
				Optional<RpgItemStack> rpgItemStack1 = ((ArmorEquipable) entity).getItemInHand(HandTypes.MAIN_HAND)
						.map(itemStack -> itemService.getRpgItemStack(itemStack)).get();
				if (rpgItemStack1.isPresent()) {
					rpgItemStack = rpgItemStack1.get();
				}
			}
		}
		newdamage *= spongeDamageService.getEntityDamageMult(attacker, source.getType());

		IEntityWeaponDamageEarlyEvent e = new IEntityWeaponDamageEarlyEvent(target, newdamage, rpgItemStack);

		e.setCause(causeStackManager.getCurrentCause());
		Sponge.getEventManager().post(e);
		if (e.isCancelled() || e.getDamage() <= 0) {
			event.setCancelled(true);
			return;
		}

		event.setBaseDamage(e.getDamage());
	}

	private void processWeaponDamageLate(DamageEntityEvent event, EntityDamageSource source, IEntity target) {
		double newdamage = event.getBaseDamage();
		newdamage *= spongeDamageService.getEntityResistance(target, source.getType());

		IEntityWeaponDamageLateEvent e = new IEntityWeaponDamageLateEvent(target, newdamage);
		e.setCause(causeStackManager.getCurrentCause());
		Sponge.getEventManager().post(e);
		if (e.isCancelled() || e.getDamage() <= 0) {
			event.setCancelled(true);
			return;
		}

		event.setBaseDamage(e.getDamage());
	}

	private void processSkillDamageEarly(DamageEntityEvent event, SkillDamageSource source, IEntity attacker, IEntity target) {
		ISkill skill = source.getSkill();
		DamageType type = source.getType();
		IEffect effect = source.getEffect();
		if (attacker.getType() == IEntityType.CHARACTER) {
			IActiveCharacter c = (IActiveCharacter) attacker;
			if (c.hasPreferedDamageType()) {
				type = c.getDamageType();
			}
		}
		double newdamage = event.getBaseDamage() * spongeDamageService.getEntityDamageMult(attacker, type);

		try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
			if (effect != null) {
				causeStackManager.pushCause(effect);
			}
			if (skill != null) {
				causeStackManager.pushCause(skill);
			}

			IEntitySkillDamageEarlyEvent e = new IEntitySkillDamageEarlyEvent(target, skill, newdamage);
			e.setCause(causeStackManager.getCurrentCause());
			Sponge.getEventManager().post(e);
			if (e.isCancelled() || e.getDamage() <= 0) {
				event.setCancelled(true);
				return;
			}

			event.setBaseDamage(e.getDamage());
		}
	}

	private void processSkillDamageLate(DamageEntityEvent event, SkillDamageSource source, IEntity attacker, IEntity target) {
		ISkill skill = source.getSkill();
		DamageType type = source.getType();
		IEffect effect = source.getEffect();
		double newdamage = event.getBaseDamage();

		try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
			if (effect != null) {
				causeStackManager.pushCause(effect);
			}
			if (skill != null) {
				causeStackManager.pushCause(skill);
			}
			newdamage *= spongeDamageService.getEntityResistance(target, type);

			IEntitySkillDamageLateEvent e = new IEntitySkillDamageLateEvent(target, skill, newdamage);
			e.setCause(causeStackManager.getCurrentCause());
			Sponge.getEventManager().post(e);
			if (e.isCancelled() || e.getDamage() <= 0) {
				event.setCancelled(true);
				return;
			}

			event.setBaseDamage(e.getDamage());
		}
	}

	private void processProjectileDamageEarly(DamageEntityEvent event, IndirectEntityDamageSource source, IEntity attacker, IEntity target, Projectile projectile) {
		double newdamage = event.getBaseDamage();
		if (attacker.getType() == IEntityType.CHARACTER) {
			IActiveCharacter c = (IActiveCharacter) attacker;
			newdamage = spongeDamageService.getCharacterProjectileDamage(c, projectile.getType());
		} else if (attacker.getType() == IEntityType.MOB) {
			if (!pluginConfig.OVERRIDE_MOBS) {
				newdamage = entityService.getMobDamage(attacker.getEntity());
			}
		}

		IEntityProjectileDamageEarlyEvent hit = new IEntityProjectileDamageEarlyEvent(target, newdamage, projectile);
		hit.setCause(causeStackManager.getCurrentCause());
		Sponge.getEventManager().post(hit);
		if (hit.isCancelled() || hit.getDamage() <= 0) {
			event.setCancelled(true);
			return;
		}

		event.setBaseDamage(hit.getDamage());
	}

	private void processProjectileDamageLate(DamageEntityEvent event, IndirectEntityDamageSource source, IEntity attacker, IEntity target, Projectile projectile) {
		double newdamage = event.getBaseDamage();
		newdamage *= spongeDamageService.getEntityResistance(target, source.getType());

		IEntityProjectileDamageLateEvent hit = new IEntityProjectileDamageLateEvent(target, newdamage, projectile);
		hit.setCause(causeStackManager.getCurrentCause());
		Sponge.getEventManager().post(hit);
		if (hit.isCancelled() || hit.getDamage() <= 0) {
			event.setCancelled(true);
			return;
		}

		event.setBaseDamage(hit.getDamage());
	}
}
