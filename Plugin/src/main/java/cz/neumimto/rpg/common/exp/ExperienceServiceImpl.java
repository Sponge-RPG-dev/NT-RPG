package cz.neumimto.rpg.common.exp;

import cz.neumimto.rpg.api.exp.IExperienceService;

import java.util.HashMap;
import java.util.Map;

public abstract class ExperienceServiceImpl implements IExperienceService {


    protected Map<String, Double> minerals = new HashMap<>();
    protected Map<String, Double> woodenBlocks = new HashMap<>();
    protected Map<String, Double> farming = new HashMap<>();
    protected Map<String, Double> fishing = new HashMap<>();

    public Double getMinningExperiences(String type) {
        return minerals.get(type);
    }

    public Double getLoggingExperiences(String type) {
        return minerals.get(type);
    }


    public Double getFishingExperience(String type) {
        return fishing.get(type);
    }

    public Double getFarmingExperiences(String type) {
        return farming.get(type);
    }
}
