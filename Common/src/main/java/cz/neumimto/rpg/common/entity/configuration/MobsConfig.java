package cz.neumimto.rpg.common.entity.configuration;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 7.5.2018.
 */
public class MobsConfig {

    @Path("damage")
    private Map<String, Double> damage;

    @Path("experiences")
    private Map<String, Double> experiences;

    @Path("health")
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
