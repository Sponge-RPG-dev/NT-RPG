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

package cz.neumimto.skills;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillInfo {
    public static SkillInfo EMPTY = new SkillInfo("Empty") {{
        setSkillSettings(new SkillSettings());
    }};
    private final String skill;
    private SkillSettings skillSettings;
    private int minPlayerLevel;
    private int maxSkillLevel;
    private Set<SkillInfo> softDepends = new HashSet<>();
    private Set<SkillInfo> hardDepends = new HashSet<>();
    private Set<SkillInfo> conflicts = new HashSet<>();
    private Set<SkillInfo> depending = new HashSet<>();

    public SkillInfo(String skill) {
        this.skill = skill;

    }

    public String getSkillName() {
        return skill;
    }

    public boolean conflictsWith(SkillInfo skillInfo) {
        return getConflicts().contains(skillInfo.getSkillName());
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

    public Set<SkillInfo> getSoftDepends() {
        return softDepends;
    }

    public Set<SkillInfo> getHardDepends() {
        return hardDepends;
    }

    public Set<SkillInfo> getConflicts() {
        return conflicts;
    }

    public void setSoftDepends(Set<SkillInfo> softDepends) {
        this.softDepends = softDepends;
    }

    public void setHardDepends(Set<SkillInfo> hardDepends) {
        this.hardDepends = hardDepends;
    }

    public void setConflicts(Set<SkillInfo> conflicts) {
        this.conflicts = conflicts;
    }

    public Set<SkillInfo> getDepending() {
        return depending;
    }
}
