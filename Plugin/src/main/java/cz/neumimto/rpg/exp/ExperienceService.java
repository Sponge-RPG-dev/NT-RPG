package cz.neumimto.rpg.exp;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.players.ExperienceSource;
import cz.neumimto.rpg.players.ExperienceSources;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.Fish;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static cz.neumimto.rpg.Log.warn;

/**
 * Created by NeumimTo on 8.4.2017.
 */
@Singleton
public class ExperienceService {

	private Map<BlockType, Double> minerals = new HashMap<>();
	private Map<BlockType, Double> woodenBlocks = new HashMap<>();

	@Inject
	private ExperienceDAO experienceDAO;
	private Map<Fish, Double> fishing = new HashMap<>();

	public void load() {
		Map<String, Double> experiencesForMinerals = experienceDAO.getExperiencesForMinerals();

		for (Map.Entry<String, Double> entry : experiencesForMinerals.entrySet()) {
			Optional<BlockType> type = Sponge.getGame().getRegistry().getType(BlockType.class, entry.getKey());
			if (type.isPresent()) {
				minerals.put(type.get(), entry.getValue());
			} else {
				warn("Unknown block type: " + entry.getKey());
			}
		}

		Map<String, Double> experiencesForWoodenBlocks = experienceDAO.getExperiencesForWoodenBlocks();
		for (Map.Entry<String, Double> entry : experiencesForWoodenBlocks.entrySet()) {
			Optional<BlockType> type = Sponge.getGame().getRegistry().getType(BlockType.class, entry.getKey());
			if (type.isPresent()) {
				woodenBlocks.put(type.get(), entry.getValue());
			} else {
				warn("Unknown block type: " + entry.getKey());
			}
		}

		Map<String, Double> fe = experienceDAO.getExperiencesForFishing();
		for (Map.Entry<String, Double> entry : fe.entrySet()) {
			Optional<Fish> type = Sponge.getGame().getRegistry().getType(Fish.class, entry.getKey());
			if (type.isPresent()) {
				fishing.put(type.get(), entry.getValue());
			} else {
				warn("Unknown entity type: " + entry.getKey());
			}
		}

	}


	public Double getMinningExperiences(BlockType type) {
		return minerals.get(type);
	}

	public Double getLoggingExperiences(BlockType type) {
		return minerals.get(type);
	}

	public ExperienceSource getExperienceSourceByBlockType(BlockType type) {
		if (minerals.containsKey(type)) {
			return ExperienceSources.MINING;
		}
		if (woodenBlocks.containsKey(type)) {
			return ExperienceSources.LOGGING;
		}
		return null;
	}

	public Double getFishingExperience(Fish type) {
		return fishing.get(type);
	}
}
