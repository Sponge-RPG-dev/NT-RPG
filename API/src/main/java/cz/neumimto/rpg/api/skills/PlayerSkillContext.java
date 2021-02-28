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
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.logging.Log;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;
import parsii.tokenizer.ParseException;

import java.util.*;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class PlayerSkillContext {

    public static final String LEVEL_KEY = "level";
    public static final String CLASS_LEVEL_KEY = "classLevel";
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

    private Object2DoubleOpenHashMap<String> cachedComputedSkillSettings;
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

    public Object2DoubleOpenHashMap<String> getCachedComputedSkillSettings() {
        if (cachedComputedSkillSettings == null) {
            SkillSettings preSet = skillData.getSkillSettings();

            Scope scope = new Scope();
            Variable level = scope.getVariable(LEVEL_KEY);
            level.setValue(getTotalLevel());

            Variable classLevel = scope.getVariable(CLASS_LEVEL_KEY);
            if (classDefinition != null) {
                PlayerClassData classByName = character.getClassByName(classDefinition.getName());
                if (classByName != null) {
                    classLevel.setValue(classByName.getLevel());
                } else {
                    classLevel.setValue(0);
                }
            }

            for (String attId : Rpg.get().getPropertyService().getAttributes().keySet()) {
                Variable variable = scope.getVariable(attId);
                variable.setValue(character.getAttributeValue(attId));
            }

            int initial = previousSize;
            cachedComputedSkillSettings = new Object2DoubleOpenHashMap<>(initial, 0.1f);

            populateCache(preSet, scope);
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
                scope.getVariable(LEVEL_KEY).setValue(upg.getTotalLevel());
                populateCache(ssUpgrade, scope);
            }
        }
        return cachedComputedSkillSettings;
    }

    private double getLevelNodeValue(String s) {
        return getCachedComputedSkillSettings().getDouble(s);
    }

    public double getFloatNodeValue(ISkillNode node) {
        return getFloatNodeValue(node.value());
    }

    public double getFloatNodeValue(String node) {
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

    public void populateCache(SkillSettings settings, Scope scope) {
        for (Map.Entry<String, String> entry : settings.getNodes().entrySet()) {
            try {
                Expression parse = Parser.parse(entry.getValue(), scope);
                double evaluate = parse.evaluate();
                cachedComputedSkillSettings.put(entry.getKey(), evaluate);
            } catch (ParseException e) {
                Log.error("Could not parse expression " + entry.getValue());
            }
        }
    }

    public void invalidateSkillSettingsCache() {
        this.cachedComputedSkillSettings = null;
    }
}
