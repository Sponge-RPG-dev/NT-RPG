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
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import java.util.*;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class PlayerSkillContext {

    public static final PlayerSkillContext EMPTY;

    static {
        EMPTY = new PlayerSkillContext(null, null, null);
        EMPTY.setSkillData(SkillData.EMPTY);
    }

    private final IActiveCharacter character;

    private int level;
    private SkillData skillData;
    private int bonusLevel;

    private final ClassDefinition classDefinition;
    private ISkill skill;

    private Object2FloatOpenHashMap<String> cachedComputedSkillSettings;
    private int previousSize = 0;

    public PlayerSkillContext(ClassDefinition classDefinition, ISkill skill, IActiveCharacter character) {
        this.classDefinition = classDefinition;
        this.skill = skill;
        this.character = character;
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

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

    public Object2FloatOpenHashMap<String> getCachedComputedSkillSettings() {
        if (cachedComputedSkillSettings == null) {
            SkillSettings preSet = skillData.getSkillSettings();

            int initial = previousSize;
            cachedComputedSkillSettings = new Object2FloatOpenHashMap<>(initial, 0.1f);

            Set<String> complexKeySuffixes = SkillSettings.getComplexKeySuffixes();

            Collection<AttributeConfig> attributes = Rpg.get().getPropertyService().getAttributes().values();

            populateCache(complexKeySuffixes, attributes, preSet, getTotalLevel());
            if (previousSize == 0) {
                previousSize = cachedComputedSkillSettings.size();
            }

            Set<SkillData> upgradedBy = skillData.getUpgradedBy();
            for (SkillData upgrade : upgradedBy) {
                PlayerSkillContext upg = character.getSkillInfo(upgrade.getSkillId());
                if (upg == null) {
                    continue;
                }
                SkillSettings ssUpgrade = upgrade.getUpgrades().get(skillData.getSkillId());
                populateCache(complexKeySuffixes, attributes, ssUpgrade, upg.getTotalLevel());
            }
        }
        return cachedComputedSkillSettings;
    }

    private float getLevelNodeValue(String s) {
        return getCachedComputedSkillSettings().getFloat(s);
    }

    public float getFloatNodeValue(ISkillNode node) {
        return getFloatNodeValue(node.value());
    }

    public float getFloatNodeValue(String node) {
        return getLevelNodeValue(node);
    }

    public boolean hasNode(String node) {
        return cachedComputedSkillSettings.containsKey(node);
    }

    public int getIntNodeValue(ISkillNode node) {
        return getIntNodeValue(node.value());
    }

    public int getIntNodeValue(String node) {
        return (int) getLevelNodeValue(node);
    }

    public long getLongNodeValue(ISkillNode node) {
        return getLongNodeValue(node.value());
    }

    public long getLongNodeValue(String node) {
        return (long) getLevelNodeValue(node);
    }

    public double getDoubleNodeValue(String node) {
        return getLevelNodeValue(node);
    }

    public double getDoubleNodeValue(ISkillNode node) {
        return getDoubleNodeValue(node.value());
    }

    public void populateCache(Set<String> complexKeySuffixes, Collection<AttributeConfig> attributes, SkillSettings settings, int level) {

        for (Map.Entry<String, Float> entry : settings.getNodes().entrySet()) {
            cacheComplexNodes(complexKeySuffixes, attributes, entry, level);
        }
    }

    private void cacheComplexNodes(Set<String> complexKeySuffixes, Collection<AttributeConfig> attributes, Map.Entry<String, Float> entry, int level) {
        String key = entry.getKey();
        Optional<String> first = isComplexNode(complexKeySuffixes, key);

        if (first.isPresent()) {
            String s = first.get();
            if (s.endsWith(SkillSettings.BONUS_SUFFIX)) {
                cacheSettingsNodes(entry, s, level);
            }
            cacheAttributeNodes(attributes, entry, s);
        } else {
            float val = cachedComputedSkillSettings.getOrDefault(entry.getKey(), 0f);
            cachedComputedSkillSettings.put(entry.getKey(), val + entry.getValue().floatValue());
        }
    }

    private void cacheSettingsNodes(Map.Entry<String, Float> entry, String s, int level) {
        String stripped = s.substring(0, s.length() - SkillSettings.BONUS_SUFFIX.length());
        float aFloat1 = cachedComputedSkillSettings.getFloat(stripped);
        cachedComputedSkillSettings.put(stripped, aFloat1 + entry.getValue() * level);
    }

    private void cacheAttributeNodes(Collection<AttributeConfig> attributes, Map.Entry<String, Float> entry, String s) {
        for (AttributeConfig attribute : attributes) {
            String id = "_per_" + attribute.getId();
            if (s.endsWith(id)) {
                String stripped = s.substring(0, s.length() - id.length());
                float aFloat1 = cachedComputedSkillSettings.getFloat(stripped);
                cachedComputedSkillSettings.put(stripped, aFloat1 + entry.getValue() * character.getAttributeValue(attribute));
                break;
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
