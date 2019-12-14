package cz.neumimto.rpg.spigot.exp;

import cz.neumimto.rpg.common.exp.AbstractExperienceService;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import javax.inject.Singleton;
import java.util.Map;

import static cz.neumimto.rpg.api.logging.Log.warn;

@Singleton
public class SpigotExperienceService extends AbstractExperienceService {

    @Override
    public void load() {
        super.load();

        Map<String, Double> fe = experienceDAO.getExperiencesForFishing();
        for (Map.Entry<String, Double> entry : fe.entrySet()) {
            try {
                EntityType fished = EntityType.valueOf(entry.getKey());
                fishing.put(fished.name(), entry.getValue());
            } catch (IllegalArgumentException e) {
                warn("Unknown entity type: " + entry.getKey());
            }
        }
    }

    @Override
    public void populateBlockCacheFromConfig(Map<String, Double> expMap, Map<String, Double> map) {
        for (Map.Entry<String, Double> entry : expMap.entrySet()) {
            Material material = Material.matchMaterial(entry.getKey());
            if (material != null) {
                map.put(material.name(), entry.getValue());
            } else {
                warn("Unknown block type: " + entry.getKey());
            }
        }
    }
}
