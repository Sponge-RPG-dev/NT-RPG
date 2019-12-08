package cz.neumimto.rpg.common.exp;

import cz.neumimto.rpg.api.exp.ExperienceService;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public abstract class AbstractExperienceService implements ExperienceService {

    @Inject
    protected ExperienceDAO experienceDAO;

    protected Map<String, Double> minerals = new HashMap<>();
    protected Map<String, Double> woodenBlocks = new HashMap<>();
    protected Map<String, Double> farming = new HashMap<>();
    protected Map<String, Double> fishing = new HashMap<>();

    @Override
    public Double getMiningExperiences(String type) {
        return minerals.get(type);
    }

    @Override
    public Double getLoggingExperiences(String type) {
        return minerals.get(type);
    }

    @Override
    public Double getFishingExperience(String type) {
        return fishing.get(type);
    }

    @Override
    public Double getFarmingExperiences(String type) {
        return farming.get(type);
    }

    @Override
    public void load() {
        Map<String, Double> experiencesForMinerals = experienceDAO.getExperiencesForMinerals();
        populateBlockCacheFromConfig(experiencesForMinerals, minerals);

        Map<String, Double> experiencesForWoodenBlocks = experienceDAO.getExperiencesForWoodenBlocks();
        populateBlockCacheFromConfig(experiencesForWoodenBlocks, woodenBlocks);

        Map<String, Double> fm = experienceDAO.getExperiencesForFarming();
        populateBlockCacheFromConfig(fm, farming);
    }

    @Override
    public void reload() {
        minerals.clear();
        woodenBlocks.clear();
        farming.clear();
        fishing.clear();

        load();
    }

    public abstract void populateBlockCacheFromConfig(Map<String, Double> expMap, Map<String, Double> map);
}
