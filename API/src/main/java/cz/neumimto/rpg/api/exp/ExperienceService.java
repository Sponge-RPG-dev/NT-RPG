package cz.neumimto.rpg.api.exp;

public interface ExperienceService {

    Double getMiningExperiences(String type);

    Double getLoggingExperiences(String type);

    Double getFishingExperience(String type);

    Double getFarmingExperiences(String type);

    void load();
}
