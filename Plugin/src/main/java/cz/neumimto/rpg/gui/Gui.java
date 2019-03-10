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
import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.LocalizableParametrizedText;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.common.def.ClickComboActionComponent;
import cz.neumimto.rpg.inventory.CannotUseItemReason;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.tree.SkillTree;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;

/**
 * Created by NeumimTo on 12.2.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class Gui {

	public static IPlayerMessage vanilla;

	public static IPlayerMessage mod;

	static {
		vanilla = IoC.get().build(VanillaMessaging.class);
	}

	public static IPlayerMessage getMessageTypeOf(IActiveCharacter player) {
		if (player == null || player.isUsingGuiMod()) {
			return mod;
		}
		return vanilla;
	}

	public static IPlayerMessage getMessageTypeOf(Player player) {
		if (mod == null) {
			return vanilla;
		}
   /*     if (isUsingClientSideGui(player))
			return mod;*/
		return vanilla;
	}

	public static void sendMessage(IActiveCharacter player, LocalizableParametrizedText message, Arg arg) {
		getMessageTypeOf(player).sendMessage(player, message, arg);
	}

	public static void sendCooldownMessage(IActiveCharacter player, String skillname, double cooldown) {
		getMessageTypeOf(player).sendCooldownMessage(player, skillname, cooldown);
	}

	public static void openSkillTreeMenu(IActiveCharacter player) {
		getMessageTypeOf(player).openSkillTreeMenu(player);
	}

	public static void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
		getMessageTypeOf(player).sendEffectStatus(player, type, effect);
	}

	public static void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {
		getMessageTypeOf(player).invokeCharacterMenu(player, characterBases);
	}

	public static void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {
		getMessageTypeOf(character).sendPlayerInfo(character, target);
	}

	public static void showCharacterInfo(IActiveCharacter player, IActiveCharacter character) {
		getMessageTypeOf(player).sendPlayerInfo(player, character);
	}

	public static void showExpChange(IActiveCharacter character, String classname, double expchange) {
		getMessageTypeOf(character).showExpChange(character, classname, expchange);
	}

	public static void showLevelChange(IActiveCharacter character, PlayerClassData aClass, int level) {
		getMessageTypeOf(character).showLevelChange(character, aClass, level);
	}

	public static void sendStatus(IActiveCharacter character) {
		getMessageTypeOf(character).sendStatus(character);
	}

	public static void invokeDefaultMenu(IActiveCharacter character) {
		getMessageTypeOf(character).invokerDefaultMenu(character);
	}

	public static void sendListOfCharacters(IActiveCharacter player, CharacterBase currentlyCreated) {
		getMessageTypeOf(player).sendListOfCharacters(player, currentlyCreated);
	}

	public static void showClassInfo(IActiveCharacter character, ClassDefinition cc) {
		getMessageTypeOf(character).showClassInfo(character, cc);
	}

	public static void sendListOfRunes(IActiveCharacter character) {
		getMessageTypeOf(character).sendListOfRunes(character);
	}

	public static void displayClassArmor(ClassDefinition g, Player target) {
		getMessageTypeOf(target).displayGroupArmor(g, target);
	}

	public static void displayClassWeapons(ClassDefinition g, Player target) {
		getMessageTypeOf(target).displayGroupWeapon(g, target);
	}

	public static void displayInitialAttributes(ClassDefinition g, Player target) {
		getMessageTypeOf(target).displayAttributes(target, g);
	}

	public static void displayRuneword(IActiveCharacter character, RuneWord rw) {
		getMessageTypeOf(character).displayRuneword(character, rw, true);
	}

	public static void displayRunewordBlockedGroups(IActiveCharacter character, RuneWord rw) {
		getMessageTypeOf(character).displayRunewordBlockedGroups(character, rw);
	}

	public static void displayRunewordRequiredGroups(IActiveCharacter character, RuneWord rw) {
		getMessageTypeOf(character).displayRunewordRequiredGroups(character, rw);
	}

	public static void displayRunewordAllowedGroups(IActiveCharacter character, RuneWord rw) {
		getMessageTypeOf(character).displayRunewordAllowedGroups(character, rw);
	}

	public static void displayRunewordAllowedItems(IActiveCharacter character, RuneWord rw) {
		getMessageTypeOf(character).displayRunewordAllowedItems(character, rw);
	}

	public static void displayHealth(IActiveCharacter character) {
		getMessageTypeOf(character).displayHealth(character);
	}

	public static void displayMana(IActiveCharacter character) {
		getMessageTypeOf(character).displayMana(character);
	}

	public static void sendCannotUseItemNotification(IActiveCharacter character, ItemStack is, CannotUseItemReason reason) {
		getMessageTypeOf(character).sendCannotUseItemNotification(character, is, reason);
	}

	public static void sendNotification(IActiveCharacter character, Text text) {
		character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, text);
	}

	public static void displayCurrentClicks(IActiveCharacter character, String combo) {

		String split = combo.replaceAll(".", "$0 ");
		LiteralText build = Text.builder(split).color(TextColors.GREEN).style(TextStyles.UNDERLINE)
				.append(Text.builder("_").color(TextColors.GRAY).build()).build();

		character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, build);

	}

	public static void resetCurrentClicks(ClickComboActionComponent clickComboActionComponent, boolean byShift) {
		clickComboActionComponent.getConsumer().sendMessage(ChatTypes.ACTION_BAR, Localizations.CANCELLED.toText());

	}

	public static void moveSkillTreeMenu(IActiveCharacter character) {
		getMessageTypeOf(character).moveSkillTreeMenu(character);
	}

	public static void displaySkillDetailsInventoryMenu(IActiveCharacter character, SkillTree tree, String command) {
		getMessageTypeOf(character).displaySkillDetailsInventoryMenu(character, tree, command);
	}

	public static void displayInitialProperties(ClassDefinition byName, Player commandSource) {
		getMessageTypeOf(commandSource).displayInitialProperties(byName, commandSource);
	}

	public static void sendCannotUseItemInOffHandNotification(IActiveCharacter character, ItemStack futureOffHand, CannotUseItemReason reason) {
		getMessageTypeOf(character).sendCannotUseItemInOffHandNotification(futureOffHand, character, reason);
	}

	public static void skillExecution(IActiveCharacter character, PlayerSkillContext skill) {
		getMessageTypeOf(character).skillExecution(character, skill);
	}

	public static void sendClassesByType(IActiveCharacter character, String o) {
		getMessageTypeOf(character).sendClassesByType(character, o);
	}

	public static void sendClassTypes(IActiveCharacter character) {
		getMessageTypeOf(character).sendClassTypes(character);
	}
}
