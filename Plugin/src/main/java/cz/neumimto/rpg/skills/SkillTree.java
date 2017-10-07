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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillTree {
	public static SkillTree Default = new SkillTree() {{
		setId("None");
		setDescription("No skill tree");
	}};
	private String id;
	private Map<String, SkillData> skills = new HashMap<>();
	private String description;
	private short[][] skillTreeMap;


	public Map<String, SkillData> getSkills() {
		return skills;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SkillData getSkillById(int id) {
		for (SkillData skillData : skills.values()) {
			if (skillData.getSkillTreeId() == id) {
				return skillData;
			}
		}
		return null;
	}

	public void setSkillTreeMap(short[][] skillTreeMap) {
		this.skillTreeMap = skillTreeMap;
	}

	public short[][] getSkillTreeMap() {
		return skillTreeMap;
	}
}
