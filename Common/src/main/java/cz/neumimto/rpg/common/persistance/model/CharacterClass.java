package cz.neumimto.rpg.common.persistance.model;

public class CharacterClass extends TimestampEntity {

    private Long classId;
    private double experiences;
    private int level;
    private String name;
    private int usedSkillPoints;
    private int skillPoints;

    private CharacterBase characterBase;

    public Long getId() {
        return classId;
    }

    public void setId(Long id) {
        this.classId = id;
    }

    public CharacterBase getCharacterBase() {
        return characterBase;
    }

    public void setCharacterBase(CharacterBase characterBase) {
        this.characterBase = characterBase;
    }

    public double getExperiences() {
        return experiences;
    }

    public void setExperiences(double experiences) {
        this.experiences = experiences;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getUsedSkillPoints() {
        return usedSkillPoints;
    }

    public void setUsedSkillPoints(int usedSkillPoints) {
        this.usedSkillPoints = usedSkillPoints;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CharacterClass that = (CharacterClass) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return classId != null ? classId.hashCode() : name.hashCode();
    }

    @Override
    public String toString() {
        return "CharacterClassImpl{" +
                "classId=" + classId +
                ", experiences=" + experiences +
                ", level=" + level +
                ", name='" + name + '\'' +
                ", usedSkillPoints=" + usedSkillPoints +
                ", skillPoints=" + skillPoints +
                ", characterBase=" + characterBase +
                '}';
    }
}
