/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.api.entity.players.classes;

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
        return levelProgression != null && (characterClass.getLevel() < levelProgression.getMaxLevel()
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
