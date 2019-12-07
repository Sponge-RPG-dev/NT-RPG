package cz.neumimto.rpg.sponge.exp;

import cz.neumimto.rpg.common.exp.AbstractExperienceService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.Fish;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

import static cz.neumimto.rpg.api.logging.Log.warn;

/**
 * Created by NeumimTo on 8.4.2017.
 */
@Singleton
public class SpongeExperienceService extends AbstractExperienceService {

    @Override
    public void load() {
        super.load();

        Map<String, Double> fe = experienceDAO.getExperiencesForFishing();
        for (Map.Entry<String, Double> entry : fe.entrySet()) {
            Optional<Fish> type = Sponge.getGame().getRegistry().getType(Fish.class, entry.getKey());
            if (type.isPresent()) {
                fishing.put(type.get().getId(), entry.getValue());
            } else {
                warn("Unknown entity type: " + entry.getKey());
            }
        }
    }

    public void populateBlockCacheFromConfig(Map<String, Double> expMap, Map<String, Double> map) {
        for (Map.Entry<String, Double> entry : expMap.entrySet()) {
            Optional<BlockType> type = Sponge.getGame().getRegistry().getType(BlockType.class, entry.getKey());
            if (type.isPresent()) {
                map.put(type.get().getId(), entry.getValue());
            } else {
                warn("Unknown block type: " + entry.getKey());
            }
        }
    }
}
