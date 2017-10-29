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

import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.inventory.CannotUseItemReson;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.skills.SkillTree;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public interface IPlayerMessage {
	boolean isClientSideGui();

	void sendMessage(IActiveCharacter player, String message);

	void sendCooldownMessage(IActiveCharacter player, String message, double cooldown);


	void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect);

	void invokeCharacterMenu(Player player, List<CharacterBase> characterBases);

	void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target);

	void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target);

	void showExpChange(IActiveCharacter character, String classname, double expchange);

	void showLevelChange(IActiveCharacter character, ExtendedNClass clazz, int level);

	void sendStatus(IActiveCharacter character);

	void showAvalaibleClasses(IActiveCharacter character);

	void invokerDefaultMenu(IActiveCharacter character);

	void sendListOfCharacters(IActiveCharacter player, CharacterBase currentlyCreated);

	void showClassInfo(IActiveCharacter character, ConfigClass cc);

	void sendListOfRunes(IActiveCharacter character);

	void sendListOfRaces(IActiveCharacter target);

	void sendRaceInfo(IActiveCharacter target, Race race);

	void displayGroupArmor(PlayerGroup g, Player target);

	void displayGroupWeapon(PlayerGroup g, Player target);

	void sendClassInfo(IActiveCharacter target, ConfigClass configClass);

	void displayAttributes(Player target, PlayerGroup group);

	void displayRuneword(IActiveCharacter character, RuneWord rw, boolean linkToRWList);

	void displayRunewordBlockedGroups(IActiveCharacter character, RuneWord rw);

	void displayRunewordRequiredGroups(IActiveCharacter character, RuneWord rw);

	void displayRunewordAllowedGroups(IActiveCharacter character, RuneWord rw);

	void displayRunewordAllowedItems(IActiveCharacter character, RuneWord rw);

	void displayHealth(IActiveCharacter character);

	void displayMana(IActiveCharacter character);

	void sendCannotUseItemNotification(IActiveCharacter character, ItemStack is, CannotUseItemReson reason);

	void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree);

	void moveSkillTreeMenu(IActiveCharacter character);

	void displaySkillDetailsInventoryMenu(IActiveCharacter character, SkillTree tree, String command);
}
