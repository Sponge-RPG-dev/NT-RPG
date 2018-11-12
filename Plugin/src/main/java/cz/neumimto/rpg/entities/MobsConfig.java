package cz.neumimto.rpg.entities;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 7.5.2018.
 */
@ConfigSerializable
public class MobsConfig {

	@Setting(value = "damage", comment = "Entity Damage")
	private Map<EntityType, Double> damage;

	@Setting(value = "experiences", comment = "Entity experience gain")
	private Map<EntityType, Double> experiences;

	@Setting(value = "health", comment = "Entity Maximum health")
	private Map<EntityType, Double> health;

	public MobsConfig() {
		this.damage = new HashMap<>();
		this.experiences = new HashMap<>();
		this.health = new HashMap<>();
	}

	public Map<EntityType, Double> getDamage() {
		return damage;
	}

	public void setDamage(Map<EntityType, Double> damage) {
		this.damage = damage;
	}

	public Map<EntityType, Double> getExperiences() {
		return experiences;
	}

	public void setExperiences(Map<EntityType, Double> experiences) {
		this.experiences = experiences;
	}

	public Map<EntityType, Double> getHealth() {
		return health;
	}

	public void setHealth(Map<EntityType, Double> health) {
		this.health = health;
	}
}
