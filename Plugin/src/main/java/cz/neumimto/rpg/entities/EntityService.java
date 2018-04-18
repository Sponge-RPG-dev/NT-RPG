package cz.neumimto.rpg.entities;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.events.skills.SkillHealEvent;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by NeumimTo on 19.12.2015.
 */
@Singleton
public class EntityService {

	private HashMap<UUID, IMob> entityHashMap = new HashMap<>();
	private Map<EntityType, Double> entityDamages = new HashMap<>();
	private Map<EntityType, Double> entityHealth = new HashMap<>();
	private Map<EntityType, Double> entityExperiences = new HashMap<>();

	@Inject
	private CharacterService service;

	@Inject
	private MobSettingsDao dao;

	@Inject
	private PropertyService propertyService;

	@Inject
	private EffectService effectService;

	public IEntity get(Entity id) {
		if (id.getType() == EntityTypes.PLAYER) {
			return service.getCharacter(id.getUniqueId());
		}
		IMob iEntity = entityHashMap.get(id.getUniqueId());
		if (iEntity == null) {
			iEntity = new NEntity();
			iEntity.setExperiences(-1);
			iEntity.attach((Living) id);
			entityHashMap.put(id.getUniqueId(), iEntity);
			if (!PluginConfig.OVERRIDE_MOBS) {
				id.offer(Keys.MAX_HEALTH, entityHealth.get(id.getType()));
				id.offer(Keys.HEALTH, entityHealth.get(id.getType()));
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

	public double getMobDamage(EntityType type) {
		Double d = entityDamages.get(type);
		if (d == null) return 0;
		return d;
	}

	public double getMobHealth(EntityType type) {
		Double d = entityHealth.get(type);
		if (d == null) return 0;
		return d;
	}

	@PostProcess(priority = 10)
	public void load() {

		this.entityDamages.putAll(dao.getDamages());
		this.entityHealth.putAll(dao.getHealth());
		this.entityExperiences.putAll(dao.getExperiences());

	}

	public double getExperiences(EntityType type) {
		Double d = entityExperiences.get(type);
		if (d == null) return 0;
		return d;
	}

	public float getEntityProperty(IEffectConsumer entity, int id) {
		return Math.min(entity.getProperty(id), propertyService.getMaxPropertyValue(id));
	}

	public void setEntityProperty(IEntity nEntity, int id, Float value) {
		nEntity.setProperty(id, value);
	}

	public void addToEntityProperty(IEntity nEntity, int id, Float value) {
		Float f = getEntityProperty(nEntity, id);
		setEntityProperty(nEntity, id, f == null ? value : f + value);
	}


	/**
	 * Heals the entity and`fire an event
	 *
	 * @param entity
	 * @param healedamount
	 * @return healed hp
	 */
	public double healEntity(IEntity entity, float healedamount, ISkill skill) {
		if (entity.getHealth().getValue() == entity.getHealth().getMaxValue()) {
			return 0;
		}
		SkillHealEvent event = null;
		if (entity.getHealth().getValue() + healedamount > entity.getHealth().getMaxValue()) {
			healedamount = (float) ((entity.getHealth().getValue() + healedamount) - entity.getHealth().getMaxValue());
		}
		event = new SkillHealEvent(entity, healedamount, skill);
		Sponge.getGame().getEventManager().post(event);
		if (event.isCancelled() || event.getAmount() <= 0)
			return 0;
		return setEntityHealth(event.getEntity(), event.getAmount());
	}

	/**
	 * sets character's hp to choosen amount.
	 *
	 * @param entity
	 * @param amount
	 * @return difference
	 */
	public double setEntityHealth(IEntity entity, double amount) {
		if (entity.getHealth().getValue() + amount > entity.getHealth().getMaxValue()) {
			double k = entity.getHealth().getMaxValue() - (entity.getHealth().getValue() + amount);
			setEntityToFullHealth(entity);
			return k;
		}
		entity.getHealth().setValue(entity.getHealth().getValue() + amount);
		return amount;
	}

	/**
	 * sets character to its full health
	 *
	 * @param entityToFullHealth
	 */
	public void setEntityToFullHealth(IEntity entityToFullHealth) {
		entityToFullHealth.getHealth().setValue(entityToFullHealth.getHealth().getMaxValue());
	}
}
