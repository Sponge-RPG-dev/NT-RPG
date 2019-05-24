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

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

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

    private Object2FloatOpenHashMap<String> cachedComputedSkillSettings;
    private int previousSize = 0;

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

    public Object2FloatOpenHashMap<String> getCachedComputedSkillSettings() {
        if (cachedComputedSkillSettings == null) {
            SkillSettings preSet = skillData.getSkillSettings();

            int initial = previousSize == 0 ? preSet.getNodes().size() * 3 / 4 : previousSize;
            cachedComputedSkillSettings = new Object2FloatOpenHashMap<>(initial, 0.1f);

            Set<String> complexKeySuffixes = SkillSettings.getComplexKeySuffixes();

            Collection<Attribute> attributes = Rpg.get().getAttributes();
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
            Optional<String> complexNode = isComplexNode(complexKeySuffixes, entry.getKey());
            if (!complexNode.isPresent()) {
                cachedComputedSkillSettings.put(entry.getKey(), entry.getValue().floatValue());
            }
        }

        for (Map.Entry<String, Float> entry : preSet.getNodes().entrySet()) {
            String key = entry.getKey();
            Optional<String> first = isComplexNode(complexKeySuffixes, key);

            if (first.isPresent()) {
                String s = first.get();
                if (s.endsWith(SkillSettings.bonus)) {
                    String stripped = s.substring(0, s.length() - SkillSettings.bonus.length());
                    float aFloat1 = cachedComputedSkillSettings.getFloat(stripped);
                    cachedComputedSkillSettings.put(stripped, aFloat1 + entry.getValue() * getTotalLevel());
                }
                for (Attribute attribute : attributes) {
                    String id = "_per_" + attribute.getId();
                    if (s.endsWith(id)) {
                        String stripped = s.substring(0, s.length() - id.length());
                        float aFloat1 = cachedComputedSkillSettings.getFloat(stripped);
                        cachedComputedSkillSettings.put(stripped, aFloat1 + entry.getValue() * character.getAttributeValue(attribute));
                        break;
                    }
                }
            }
        }
    }

    public Optional<String> isComplexNode(Set<String> complexKeySuffixes, String key) {
        for (String complexKeySuffix : complexKeySuffixes) {
            if (key.endsWith(complexKeySuffix)) {
                return Optional.of(key);
            }
        }
        return Optional.empty();
    }

    public void invalidateSkillSettingsCache() {
        this.cachedComputedSkillSettings = null;
    }
}
