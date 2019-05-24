package cz.neumimto.rpg.sponge.exp;

import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.common.exp.ExperienceServiceImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.Fish;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

import static cz.neumimto.rpg.api.logging.Log.warn;

/**
 * Created by NeumimTo on 8.4.2017.
 */
@Singleton
public class ExperienceService extends ExperienceServiceImpl {

    @Inject
    private ExperienceDAO experienceDAO;


    @Override
    public void load() {
        Map<String, Double> experiencesForMinerals = experienceDAO.getExperiencesForMinerals();

        for (Map.Entry<String, Double> entry : experiencesForMinerals.entrySet()) {
            Optional<BlockType> type = Sponge.getGame().getRegistry().getType(BlockType.class, entry.getKey());
            if (type.isPresent()) {
                minerals.put(type.get().getId(), entry.getValue());
            } else {
                warn("Unknown block type: " + entry.getKey());
            }
        }

        Map<String, Double> experiencesForWoodenBlocks = experienceDAO.getExperiencesForWoodenBlocks();
        for (Map.Entry<String, Double> entry : experiencesForWoodenBlocks.entrySet()) {
            Optional<BlockType> type = Sponge.getGame().getRegistry().getType(BlockType.class, entry.getKey());
            if (type.isPresent()) {
                woodenBlocks.put(type.get().getId(), entry.getValue());
            } else {
                warn("Unknown block type: " + entry.getKey());
            }
        }

        Map<String, Double> fe = experienceDAO.getExperiencesForFishing();
        for (Map.Entry<String, Double> entry : fe.entrySet()) {
            Optional<Fish> type = Sponge.getGame().getRegistry().getType(Fish.class, entry.getKey());
            if (type.isPresent()) {
                fishing.put(type.get().getId(), entry.getValue());
            } else {
                warn("Unknown entity type: " + entry.getKey());
            }
        }

        Map<String, Double> fm = experienceDAO.getExperiencesForFarming();
        for (Map.Entry<String, Double> entry : fm.entrySet()) {
            Optional<BlockType> type = Sponge.getGame().getRegistry().getType(BlockType.class, entry.getKey());
            if (type.isPresent()) {
                farming.put(type.get().getId(), entry.getValue());
            } else {
                warn("Unknown block type: " + entry.getKey());
            }
        }

    }


}
