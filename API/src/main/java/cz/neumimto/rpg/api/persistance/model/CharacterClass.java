package cz.neumimto.rpg.api.persistance.model;

public interface CharacterClass extends TimestampEntity {
    Long getId();

    void setId(Long id);

    CharacterBase getCharacterBase();

    void setCharacterBase(CharacterBase characterBase);

    double getExperiences();

    void setExperiences(double experiences);

    String getName();

    void setName(String name);

    int getSkillPoints();

    void setSkillPoints(int skillPoints);

    int getUsedSkillPoints();

    void setUsedSkillPoints(int usedSkillPoints);

    int getLevel();

    void setLevel(int level);
}
