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

import com.typesafe.config.Config;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;
import cz.neumimto.rpg.common.effects.EffectSourceType;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.util.List;
import java.util.Set;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface ISkill extends IEffectSourceProvider, IRpgElement {

    String getId();

    String getLocalizableName();

    void setLocalizableName(String name);

    void init();

    void skillLearn(IActiveCharacter character);

    void skillUpgrade(IActiveCharacter character, int level);

    void skillRefund(IActiveCharacter character);

    SkillSettings getDefaultSkillSettings();

    void onCharacterInit(IActiveCharacter c, int level);

    void onPreUse(IActiveCharacter character, SkillContext skillContext);

    Set<ISkillType> getSkillTypes();

    SkillSettings getSettings();

    void setSettings(SkillSettings settings);

    List<String> getDescription();

    void setDescription(List<String> description);

    List<String> getLore();

    void setLore(List<String> lore);

    void setIcon(String icon);

    String getDamageType();

    void setDamageType(String type);

    default String getItemType() {
        return "stone";
    }

    @Override
    default IEffectSource getType() {
        return EffectSourceType.SKILL;
    }

    default <T extends SkillData> T constructSkillData() {
        return (T) new SkillData(getName());
    }

    default <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {

    }

    default SkillContext createSkillExecutorContext(PlayerSkillContext esi) {
        return new SkillContext();
    }
}
