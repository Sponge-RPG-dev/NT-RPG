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

package cz.neumimto.rpg.players.groups;

import cz.neumimto.rpg.players.ExperienceSource;
import cz.neumimto.rpg.skills.SkillTree;
import org.spongepowered.api.util.Color;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class ConfigClass extends PlayerGroup {

    public static ConfigClass Default = new ConfigClass("CNone");

    private SkillTree skillTree = new SkillTree();

    private int skillpointsperlevel, attributepointsperlevel;
    private double[] levels;
    private double totalExp;
    private Set<ExperienceSource> experienceSourceSet = new HashSet<>();

    private Color chatColor;

    public ConfigClass(String name) {
        super(name);
    }

    public SkillTree getSkillTree() {
        return skillTree;
    }

    public void setSkillTree(SkillTree skillTree) {
        this.skillTree = skillTree;
    }

    public boolean hasExperienceSource(ExperienceSource source) {
        return experienceSourceSet.contains(source);
    }

    public double[] getLevels() {
        return levels;
    }

    public void setLevels(double[] levels) {
        this.levels = levels;
    }

    public void setExperienceSources(HashSet<ExperienceSource> experienceSources) {
        this.experienceSourceSet = experienceSources;
    }

    public double getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(double totalExp) {
        this.totalExp = totalExp;
    }

    public int getAttributepointsperlevel() {
        return attributepointsperlevel;
    }

    public void setAttributepointsperlevel(int attributepointsperlevel) {
        this.attributepointsperlevel = attributepointsperlevel;
    }

    public int getSkillpointsperlevel() {
        return skillpointsperlevel;
    }

    public void setSkillpointsperlevel(int skillpointsperlevel) {
        this.skillpointsperlevel = skillpointsperlevel;
    }

    public Color getChatColor() {
        return chatColor;
    }

    public void setChatColor(Color chatColor) {
        this.chatColor = chatColor;
    }

    public double getFirstLevelExp() {
        return levels[1];
    }

    public int getMaxLevel() {
        return levels.length -1;
    }
}
