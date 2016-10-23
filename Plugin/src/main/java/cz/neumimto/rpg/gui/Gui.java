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

package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillTree;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by NeumimTo on 12.2.2015.
 */
public class Gui {

    public static IPlayerMessage vanilla;

    public static IPlayerMessage mod;

    static {
        vanilla = IoC.get().build(VanilaMessaging.class);
    }

    public static IPlayerMessage getMessageTypeOf(IActiveCharacter player) {
        if (player.isUsingGuiMod())
            return mod;
        return vanilla;
    }

    public static IPlayerMessage getMessageTypeOf(Player player) {
        if (mod == null)
            return vanilla;
   /*     if (isUsingClientSideGui(player))
            return mod;*/
        return vanilla;
    }

    public static void sendMessage(IActiveCharacter player, String message) {
        getMessageTypeOf(player).sendMessage(player, message);
    }

    public static void sendCooldownMessage(IActiveCharacter player, String skillname, double cooldown) {
        getMessageTypeOf(player).sendCooldownMessage(player, skillname, cooldown);
    }

    public static void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, ConcurrentHashMap<String, Integer> learnedSkills) {
        getMessageTypeOf(player).openSkillTreeMenu(player, skillTree, learnedSkills);
    }

    public static void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
        getMessageTypeOf(player).sendEffectStatus(player, type, effect);
    }

    public static void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {
        getMessageTypeOf(player).invokeCharacterMenu(player, characterBases);
    }

    public static void sendManaStatus(IActiveCharacter character, double currentMana, double maxMana, double reserved) {
        getMessageTypeOf(character).sendManaStatus(character, currentMana, maxMana, reserved);
    }

    public static void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {
        getMessageTypeOf(character).sendPlayerInfo(character, target);
    }

    public static void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillData center) {
        getMessageTypeOf(player).moveSkillTreeMenu(player, skillTree, learnedSkill, center);
    }

    public static void showCharacterInfo(IActiveCharacter player, IActiveCharacter character) {
        getMessageTypeOf(player).sendPlayerInfo(player, character);
    }

    public static void showExpChange(IActiveCharacter character, String classname, double expchange) {
        getMessageTypeOf(character).showExpChange(character, classname, expchange);
    }

    public static void showLevelChange(IActiveCharacter character, ExtendedNClass aClass, int level) {
        getMessageTypeOf(character).showLevelChange(character, aClass, level);
    }

    public static void sendStatus(IActiveCharacter character) {
        getMessageTypeOf(character).sendStatus(character);
    }

    public static void showAvalaibleClasses(IActiveCharacter character) {
        getMessageTypeOf(character).showAvalaibleClasses(character);
    }

    public static void invokeDefaultMenu(IActiveCharacter character) {
        getMessageTypeOf(character).invokerDefaultMenu(character);
    }

    public static void sendListOfCharacters(IActiveCharacter player, CharacterBase currentlyCreated) {
        getMessageTypeOf(player).sendListOfCharacters(player,currentlyCreated);
    }

    public static void showClassInfo(IActiveCharacter character, ConfigClass cc) {
        getMessageTypeOf(character).showClassInfo(character,cc);
    }
}
