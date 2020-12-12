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

package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.ScriptedSkillNodeDescription;

import java.util.*;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillData {

    public final static SkillData EMPTY;

    static {
        EMPTY = new SkillData("Empty");
        EMPTY.setSkillSettings(new SkillSettings());
    }

    private final String skill;
    private SkillSettings skillSettings;

    private int minPlayerLevel;
    private int maxSkillLevel;

    private Set<SkillDependency> softDepends = new HashSet<>();
    private Set<SkillDependency> hardDepends = new HashSet<>();
    private Set<SkillData> conflicts = new HashSet<>();
    private Set<SkillData> depending = new HashSet<>();

    private ISkill iskill;
    private String combination = null;
    private int relativeX;
    private int relativeY;
    private int skillTreeId;
    private int levelGap;
    private String skillName;
    private ISkillNodeDescription description;
    private boolean useDescriptionOnly;
    private String icon;

    private Integer modelId;
    private ISkillExecutor skillExecutor;
    private Map<String, SkillCastCondition> skillCastConditions;

    private Map<String, Upgrade> skillUpgrades = new HashMap<>();

    public SkillData(String skill) {
        this.skill = skill;
    }

    public ISkill getSkill() {
        return iskill;
    }

    public void setSkill(ISkill skill) {
        this.iskill = skill;
    }

    public String getSkillId() {
        return skill;
    }

    public boolean conflictsWith(SkillData skillData) {
        return getConflicts().contains(skillData);
    }

    public SkillSettings getSkillSettings() {
        return skillSettings;
    }

    public void setSkillSettings(SkillSettings skillSettings) {
        this.skillSettings = skillSettings;
    }

    public int getMinPlayerLevel() {
        return minPlayerLevel;
    }

    public void setMinPlayerLevel(int minPlayerLevel) {
        this.minPlayerLevel = minPlayerLevel;
    }

    public int getMaxSkillLevel() {
        return maxSkillLevel;
    }

    public void setMaxSkillLevel(int maxSkillLevel) {
        this.maxSkillLevel = maxSkillLevel;
    }

    public Set<SkillDependency> getSoftDepends() {
        return softDepends;
    }

    public void setSoftDepends(Set<SkillDependency> softDepends) {
        this.softDepends = softDepends;
    }

    public Set<SkillDependency> getHardDepends() {
        return hardDepends;
    }

    public void setHardDepends(Set<SkillDependency> hardDepends) {
        this.hardDepends = hardDepends;
    }

    public Set<SkillData> getConflicts() {
        return conflicts;
    }

    public void setConflicts(Set<SkillData> conflicts) {
        this.conflicts = conflicts;
    }

    public Set<SkillData> getDepending() {
        return depending;
    }

    public String getCombination() {
        return combination;
    }

    public void setCombination(String combination) {
        this.combination = combination;
    }

    public int getRelativeX() {
        return relativeX;
    }

    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }

    public int getSkillTreeId() {
        return skillTreeId;
    }

    public void setSkillTreeId(int skillTreeId) {
        this.skillTreeId = skillTreeId;
    }

    public int getLevelGap() {
        return levelGap;
    }

    public void setLevelGap(int levelGap) {
        this.levelGap = levelGap;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public List<String> getDescription(IActiveCharacter character) {
        return description.getDescription(character);
    }

    public void setDescription(ISkillNodeDescription description) {
        this.description = description;
        useDescriptionOnly = description instanceof ScriptedSkillNodeDescription;
    }

    public boolean useDescriptionOnly() {
        return useDescriptionOnly;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public ISkillExecutor getSkillExecutor() {
        return skillExecutor;
    }

    public void setSkillExecutor(ISkillExecutor skillExecutor) {
        this.skillExecutor = skillExecutor;
    }

    public Map<String, SkillCastCondition> getSkillCastConditions() {
        return skillCastConditions;
    }

    public void setSkillCastConditions(Map<String, SkillCastCondition> skillCastConditions) {
        this.skillCastConditions = skillCastConditions;
    }

    public Map<String, Upgrade> getSkillUpgrades() {
        return skillUpgrades;
    }

    public void setSkillUpgrades(Map<String, Upgrade> skillUpgrades) {
        this.skillUpgrades = skillUpgrades;
    }

    public static class Upgrade extends SkillSettings {

    }
}
