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

package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.mods.ActiveSkillPreProcessorWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class PlayerSkillContext {

	public static PlayerSkillContext Empty = new PlayerSkillContext(null, null) {{
		setSkillData(SkillData.EMPTY);
	}};

	private int level;
	private SkillData skillData;
	private Set<ActiveSkillPreProcessorWrapper> mods = new HashSet<>();
	private int bonusLevel;

	private final ClassDefinition classDefinition;
	private ISkill skill;

	public PlayerSkillContext(ClassDefinition classDefinition, ISkill skill) {
		this.classDefinition = classDefinition;
		this.skill = skill;
	}

	public ISkill getSkill() {
		return skill;
	}

	public ClassDefinition getClassDefinition() {
		return classDefinition;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public SkillData getSkillData() {
		return skillData;
	}

	public void setSkillData(SkillData skillData) {
		this.skillData = skillData;
	}

	public int getBonusLevel() {
		return bonusLevel;
	}

	public void setBonusLevel(int bonusLevel) {
		this.bonusLevel = bonusLevel;
	}

	public int getTotalLevel() {
		return getBonusLevel() + getLevel();
	}

	public Set<ActiveSkillPreProcessorWrapper> getMods() {
		return mods;
	}

	public void setSkill(ISkill skill) {
		this.skill = skill;
	}
}
