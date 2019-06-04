package cz.neumimto.rpg.common.entity.configuration;

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
    private Map<String, Double> damage;

    @Setting(value = "experiences", comment = "Entity experience gain")
    private Map<String, Double> experiences;

    @Setting(value = "health", comment = "Entity Maximum health")
    private Map<String, Double> health;

    public MobsConfig() {
        this.damage = new HashMap<>();
        this.experiences = new HashMap<>();
        this.health = new HashMap<>();
    }

    public Map<String, Double> getDamage() {
        return damage;
    }

    public void setDamage(Map<String, Double> damage) {
        this.damage = damage;
    }

    public Map<String, Double> getExperiences() {
        return experiences;
    }

    public void setExperiences(Map<String, Double> experiences) {
        this.experiences = experiences;
    }

    public Map<String, Double> getHealth() {
        return health;
    }

    public void setHealth(Map<String, Double> health) {
        this.health = health;
    }
}
