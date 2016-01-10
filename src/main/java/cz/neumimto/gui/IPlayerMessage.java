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

package cz.neumimto.gui;

import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.ExtendedNClass;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.SkillData;
import cz.neumimto.skills.SkillTree;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public interface IPlayerMessage {
    boolean isClientSideGui();

    void sendMessage(IActiveCharacter player, String message);

    void sendCooldownMessage(IActiveCharacter player, String message, long cooldown);

    void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkills);

    void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillData center);

    void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect);

    void invokeCharacterMenu(Player player, List<CharacterBase> characterBases);

    void sendManaStatus(IActiveCharacter character, double currentMana, double maxMana, double reserved);

    void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target);

    void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target);

    void showExpChange(IActiveCharacter character,String classname,double expchange);

    void showLevelChange(IActiveCharacter character, ExtendedNClass clazz, int level);

    void sendStatus(IActiveCharacter character);

    void showAvalaibleClasses(IActiveCharacter character);
}
