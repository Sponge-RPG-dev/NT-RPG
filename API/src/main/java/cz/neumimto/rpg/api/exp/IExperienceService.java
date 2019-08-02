package cz.neumimto.rpg.api.exp;

public interface IExperienceService {

    Double getMinningExperiences(String type);

    Double getLoggingExperiences(String type);

    Double getFishingExperience(String type);

    Double getFarmingExperiences(String type);

    void load();
}
