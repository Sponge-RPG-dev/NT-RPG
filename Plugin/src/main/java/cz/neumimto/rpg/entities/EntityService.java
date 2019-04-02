package cz.neumimto.rpg.entities;

import static cz.neumimto.rpg.Log.info;
import static cz.neumimto.rpg.Log.warn;
import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.IRpgElement;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.events.skill.SkillHealEvent;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.properties.PropertyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by NeumimTo on 19.12.2015.
 */
@Singleton
public class EntityService {

	private HashMap<UUID, IMob> entityHashMap = new HashMap<>();

	@Inject
	private MobSettingsDao dao;

	@Inject
	private PropertyService propertyService;

	@Inject
	private CharacterService characterService;

	@Inject
	private EffectService effectService;

	public IEntity get(Entity id) {
		if (id.getType() == EntityTypes.PLAYER) {
			return characterService.getCharacter(id.getUniqueId());
		}
		IMob iEntity = entityHashMap.get(id.getUniqueId());
		if (iEntity == null) {
			iEntity = new NEntity((Living) id);
			iEntity.setExperiences(-1);
			entityHashMap.put(id.getUniqueId(), iEntity);
			MobsConfig dimmension = dao.getCache().getDimmension(id.getLocation().getExtent().getName());
			if (!pluginConfig.OVERRIDE_MOBS && dimmension != null) {
				Double aDouble = dimmension.getHealth().get(id.getType());
				if (aDouble == null) {
					warn("No max health configured for " + id.getType().getId() + " in world " + id.getLocation().getExtent().getName());
				} else {
					id.offer(Keys.MAX_HEALTH, aDouble);
					id.offer(Keys.HEALTH, aDouble);
				}
			}
		}

		return iEntity;

	}

	public void remove(UUID e) {
		if (entityHashMap.containsKey(e)) {
			IMob iMob = entityHashMap.get(e);
			effectService.removeAllEffects(iMob);
			entityHashMap.remove(e);
			iMob.detach();
		}
	}

	public void remove(Collection<Entity> l) {
		for (Entity a : l) {
			UUID uniqueId = a.getUniqueId();
			remove(uniqueId);
		}
	}

	public double getMobDamage(Entity type) {
		MobsConfig dimmension = dao.getCache().getDimmension(type.getLocation().getExtent().getName());
		if (dimmension != null) {
			Double aDouble = dimmension.getDamage().get(type.getType());
			if (aDouble == null) {
				warn("No damage configured for " + type.getType().getId()
						+ " in world " + type.getLocation().getExtent().getName());
				aDouble = 0D;
			}
			return aDouble;
		}
		return 0;
	}

	public double getExperiences(Entity type) {
		MobsConfig dimmension = dao.getCache().getDimmension(type.getLocation().getExtent().getName());
		if (dimmension != null) {
			Double aDouble = dimmension.getExperiences().get(type.getType());
			if (aDouble == null) {
				warn("No max experience drop configured for " + type.getType().getId()
						+ " in world " + type.getLocation().getExtent().getName());
				aDouble = 0D;
			}
			return aDouble;
		}
		return 0;
	}

	/**
	 * Unlike {@link IEntity#getProperty} this method checks for maximal allowed value, defined in config file.
	 *
	 * @see PropertyService#loadMaximalServerPropertyValues()
	 */
	public float getEntityProperty(IEffectConsumer entity, int id) {
		return Math.min(entity.getProperty(id), propertyService.getMaxPropertyValue(id));
	}

	/**
	 * Heals the entity and fire an event
	 *
	 * @param entity
	 * @param amount
	 *
	 * @return difference
	 */
	public double healEntity(IEntity entity, float amount, IRpgElement source) {
		if (entity.getHealth().getValue() == entity.getHealth().getMaxValue()) {
			return 0;
		}

		SkillHealEvent event = new SkillHealEvent(entity, amount, source);
		Sponge.getGame().getEventManager().post(event);
		if (event.isCancelled() || event.getAmount() <= 0) {
			return 0;
		}

		return setEntityHealth(event.getTarget(), entity.getHealth().getValue() + event.getAmount());
	}

	/**
	 * Sets entity's hp to chosen amount.
	 *
	 * @param entity
	 * @param value
	 *
	 * @return difference
	 */
	public double setEntityHealth(IEntity entity, double value) {
		if (value > entity.getHealth().getMaxValue()) {
			setEntityToFullHealth(entity);
			return entity.getHealth().getMaxValue() - entity.getHealth().getValue();
		}
		double diff = value - entity.getHealth().getValue();
		entity.getHealth().setValue(value);
		return diff;
	}

	/**
	 * sets entity to its full health
	 *
	 * @param entityToFullHealth
	 */
	public void setEntityToFullHealth(IEntity entityToFullHealth) {
		entityToFullHealth.getHealth().setValue(entityToFullHealth.getHealth().getMaxValue());
	}

	public void reloadMobConfiguration() {
		dao.load(null);
	}

	/**
	 * Updates character walkspeed to match DefaultProperties.walk_speed property
	 *
	 * @param entity
	 */
	public void updateWalkSpeed(IEffectConsumer entity) {
	    double speed = getEntityProperty(entity, DefaultProperties.walk_speed);
	    entity.getEntity().offer(Keys.WALKING_SPEED, speed);
	    if (pluginConfig.DEBUG.isBalance()) {
	        info(entity + " setting walk speed to " + speed);
	    }
	}
}
