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

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.mods.ActiveSkillPreProcessorWrapper;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.spongepowered.api.Sponge;

import java.util.*;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class PlayerSkillContext {

	public static PlayerSkillContext Empty = new PlayerSkillContext(null, null, null) {{
		setSkillData(SkillData.EMPTY);
	}};

    private final IActiveCharacter character;

    private int level;
	private SkillData skillData;
	private Set<ActiveSkillPreProcessorWrapper> mods;
	private int bonusLevel;

	private final ClassDefinition classDefinition;
	private ISkill skill;

	private AbstractObject2FloatMap<String> cachedComputedSkillSettings;
	private int previousSize = -1;

	public PlayerSkillContext(ClassDefinition classDefinition, ISkill skill, IActiveCharacter character) {
		this.classDefinition = classDefinition;
		this.skill = skill;
		this.character = character;
		this.mods = new HashSet<>();
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

	public AbstractObject2FloatMap<String> getCachedComputedSkillSettings() {
		if (cachedComputedSkillSettings == null) {
			SkillSettings preSet = skillData.getSkillSettings();

			int initial = previousSize == 0 ? preSet.getNodes().size()*3/4 : previousSize;
			cachedComputedSkillSettings = new Object2FloatOpenHashMap<>(initial, 0.1f);

			Set<String> complexKeySuffixes = SkillSettings.getComplexKeySuffixes();

            Collection<Attribute> attributes = Sponge.getRegistry().getAllOf(Attribute.class);
			populateCache(complexKeySuffixes, attributes);
			if (previousSize == 0) {
            	previousSize = cachedComputedSkillSettings.size();
			}
		}
		return cachedComputedSkillSettings;
	}

	public void populateCache(Set<String> complexKeySuffixes, Collection<Attribute> attributes) {
		SkillSettings preSet = skillData.getSkillSettings();
		for (Map.Entry<String, Float> entry : preSet.getNodes().entrySet()) {
			String key = entry.getKey();
			Optional<String> first = complexKeySuffixes.stream().filter(a-> !key.endsWith(a)).findFirst();
			float perLevel = 0;
			if (first.isPresent()) {
				float defaultNodeValue = entry.getValue();
				for (String complexKeySuffix : complexKeySuffixes) {
					String next = key + complexKeySuffix;

					if (complexKeySuffix.equals(SkillSettings.bonus)) {
						perLevel += preSet.getNodeValue(next);

					} else if (complexKeySuffix.contains(SkillSettings.bonus)) {
						for (Attribute attribute : attributes) {
							if (complexKeySuffix.contains(attribute.getId())) {
								if (preSet.getNodes().containsKey(next)) {
									int attributeValue = character.getAttributeValue(attribute);
									perLevel += preSet.getNodeValue(next) * attributeValue;
								}
							}
						}
					} else {
						for (Attribute attribute : attributes) {
							if (complexKeySuffix.contains(attribute.getId())) {
								if (preSet.getNodes().containsKey(next)) {
									int attributeValue = character.getAttributeValue(attribute);
									defaultNodeValue += preSet.getNodeValue(next) * attributeValue;
								}
							}
						}
					}
				}
				cachedComputedSkillSettings.put(key, defaultNodeValue + perLevel * getTotalLevel());
			}
		}
	}

	public void invalidateSkillSettingsCache() {
		this.cachedComputedSkillSettings = null;
	}
}
