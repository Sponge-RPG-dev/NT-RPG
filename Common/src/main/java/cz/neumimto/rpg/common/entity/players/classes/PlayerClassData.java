package cz.neumimto.rpg.common.entity.players.classes;

import cz.neumimto.rpg.common.entity.players.leveling.ILevelProgression;
import cz.neumimto.rpg.common.persistance.model.CharacterClass;

/**
 * Created by NeumimTo on 28.7.2015.
 */
public class PlayerClassData {

    private ClassDefinition classDefinition;
    private CharacterClass characterClass;

    public PlayerClassData(ClassDefinition classDefinition, CharacterClass characterClass) {
        this.classDefinition = classDefinition;
        this.characterClass = characterClass;
    }

    public boolean takesExp() {
        ILevelProgression levelProgression = classDefinition.getLevelProgression();
        return levelProgression != null && levelProgression.getMaxLevel() > 0 && (characterClass.getLevel() < levelProgression.getMaxLevel()
                || characterClass.getExperiences() < levelProgression.getLevelMargins()[levelProgression.getMaxLevel() - 1]);
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public void setClassDefinition(ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
    }

    public void addExperiences(double experiences) {
        characterClass.setExperiences(characterClass.getExperiences() + experiences);
    }

    public double getExperiencesFromLevel() {
        return characterClass.getExperiences();
    }

    public int getLevel() {
        return characterClass.getLevel();
    }

    public void setLevel(int level) {
        this.characterClass.setLevel(level);
    }

    public void incrementLevel() {
        setLevel(getLevel() + 1);
        characterClass.setExperiences(0);
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }
}
