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

package cz.neumimto.rpg.configuration;

import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;
import cz.neumimto.rpg.inventory.sockets.SocketTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 31.1.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "Localization.conf")
public class Localization {

	@ConfigValue
	public static String NO_MANA = "Not enough mana";

	@ConfigValue
	public static String NO_HP = "Not enough hp";

	@ConfigValue
	public static String SKILL_UPGRADED = "You've upgraded skill %skill% to level %level%";

	@ConfigValue
	public static String SKILL_LEARNED = "You've learned skill %skill%";

	@ConfigValue
	public static String SPEED_BOOST_APPLY = "speed boost applied";

	@ConfigValue
	public static String SPEED_BOOST_EXPIRE = "speed boost expired";

	@ConfigValue
	public static String PLAYER_IS_OFFLINE_MSG = "The player is offline";

	@ConfigValue
	public static String REACHED_CHARACTER_LIMIT = "You've reached character limit";

	@ConfigValue
	public static String CHARACTER_EXISTS = "Character with same name already exists.";

	@ConfigValue
	public static String NON_EXISTING_GROUP = "This group does not exists";

	@ConfigValue
	public static String NO_PERMISSIONS = "You can't do that";

	@ConfigValue
	public static String CHARACTER_IS_REQUIRED = "You need to create a character for this action";

	@ConfigValue
	public static String PLAYER_CANT_CHANGE_RACE = "You can't change race";

	@ConfigValue
	public static String PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE = "Player %player% has learned skill %skill%";

	@ConfigValue
	public static String PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE = "Player %player% has upgraded skill %skill% to level %skill%";

	@ConfigValue
	public static String PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE = "Player %player% has has refunded skill %skill%";

	@ConfigValue
	public static String PLAYER_IS_SILENCED = "You can't use this skill, you are silenced.";

	@ConfigValue
	public static String CHARACTER_DOES_NOT_HAVE_SKILL = "You dont have this skill";

	@ConfigValue
	public static String ON_COOLDOWN = "%skill% has %time% seconds of cooldown";

	@ConfigValue
	public static String CANT_USE_PASSIVE_SKILL = "You can't use passive skill";

	@ConfigValue
	public static String ALREADY_IN_PARTY = "You are already in party";

	@ConfigValue
	public static String PARTY_CREATED = "You've created a new party, use /nparty invite {player} to get a new teammembers";

	@ConfigValue
	public static String PLAYER_MSG_ON_JOIN_PARTY = "You have joined party";

	@ConfigValue
	public static String PARTY_MSG_ON_PLAYER_JOIN = "%s has joined your party";

	@ConfigValue
	public static String NON_EXISTING_CHARACTER = "Selected character does not exist";

	@ConfigValue
	public static String PLAYER_CHOOSED_CLASS = "You've chosen class %class%";

	@ConfigValue
	public static String UNKNOWN_CLASS = "Class %class% does not exist";

	@ConfigValue
	public static String PLAYER_CHOOSED_RACE = "You've chosen race %race%";

	@ConfigValue
	public static String UNKNOWN_RACE = "Race %race% does not exist";;

	@ConfigValue
	public static String ALREADY_CUURENT_CHARACTER = "This character has been already activated";

	@ConfigValue
	public static String NO_TARGET = "No target";

	@ConfigValue
	public static String CURRENT_CHARACTER = "Selected character: %character%";

	@ConfigValue
	public static String CHARACTER_GAINED_POINTS = "You've gained %skillpoints% skillpoints and %attributes% attribute points";

	@ConfigValue
	public static String NO_ITEM_IN_HAND = "An item in hand is required for this action";

	@ConfigValue
	public static String NO_ACCESS_TO_SKILL = "You dont have access to this skill.";

	@ConfigValue
	public static String CAST_SKILL_ON_RIGHTLICK = "&8Casts skill &2%skill%&8  on rightclick";

	@ConfigValue
	public static String EMPTY_HAND_REQUIRED = "An empty hand is required";

	@ConfigValue
	public static String CAST_SKILl_ON_LEFTCLICK = "&8Casts skill &2%skill%&8  on rightclick";

	@ConfigValue
	public static String ITEM_SKILLBIND_FOOTER = "For casting bounded skills you must have access :n to the skills via command /skill";

	@ConfigValue
	public static String RUNE = "Rune";

	@ConfigValue
	public static String NO_SKILLPOINTS = "You dont have any skillpoints.";

	@ConfigValue
	public static String NOT_LEARNED_SKILL = "You have to learn the skill first before spending skillpoints";

	@ConfigValue
	public static String SKILL_REQUIRES_HIGHER_LEVEL = "Upgrading the skill %skill% requires at least level %level%";

	@ConfigValue
	public static String SKILL_IS_ON_MAX_LEVEL = "The skill %skill% is on its maximal level - %level%.";

	@ConfigValue
	public static String SKILL_NOT_IN_A_TREE = "The skill %skill% is not accessible from your skill tree";

	@ConfigValue
	public static String MISSING_SKILL_DEPENDENCIES = "The skill %skill% requires [%hard%] and at least one of [%soft%]";

	@ConfigValue
	public static String SKILL_ALREADY_LEARNED = "You've already learned the skill %skill% ";

	@ConfigValue
	public static String SKILLBIND = "Bind";

	@ConfigValue
	public static String RUNE_FOOTER = "Can be inserted into socketed items";

	@ConfigValue
	public static String SOCKET = "Socketed Item";

	@ConfigValue
	public static String UNKNOWN_RUNE_NAME = "You can work with unknown types of runes. The rune has been probably removed";

	@ConfigValue
	public static String RUNEWORD = "Runeword";

	@ConfigValue
	public static String CHARM_INFO = "Keep in your hotbar to gain bonus";

	@ConfigValue
	public static String CHARM = "Charm";

	@ConfigValue
	public static String SOCKET_HELP = "Use anvil to imbue socket";

	@ConfigValue
	public static String SOCKET_CANCELLED = "Socketing cancelled";

	@ConfigValue
	public static String WEAPONS = "Weapons";

	@ConfigValue
	public static String ARMOR = "Armor";

	@ConfigValue
	public static String WEAPONS_MENU_HELP = "Displays list of allowed weapons and damage values";

	@ConfigValue
	public static String ARMOR_MENU_HELP = "Displays list of allowed armor";

	@ConfigValue
	public static String ATTRIBUTES = "Attributes";

	@ConfigValue
	public static String BACK = "Back";

	@ConfigValue
	public static String INITIAL_VALUE = "Attributes at start";

	@ConfigValue
	public static String CONFIRM = "Confirm";

	@ConfigValue
	public static String SKILL_CONFLICTS = "Skill %skill% conflicts with %conflict%";

	@ConfigValue
	public static String RUNE_LIST = "Runes";

	@ConfigValue
	public static String RUNEWORD_ITEMS_MENU_TOOLTIP = "List of items, which can be used for crafting %runeword%";

	@ConfigValue
	public static String RUNEWORD_ITEMS_MENU = "Allowed Items";

	@ConfigValue
	public static String RUNEWORD_ALLOWED_GROUPS_MENU = "Allowed Races or Classes";

	@ConfigValue
	public static String RUNEWORD_ALLOWED_GROUPS_MENU_TOOLTIP = "At least one of them character has to have";

	@ConfigValue
	public static String RUNEWORD_BLOCKED_GROUPS_MENU = "Blocked Races and Classes";

	@ConfigValue
	public static String RUNEWORD_BLOCKED_GROUPS_MENU_TOOLTIP = "Any of them has to character have";

	@ConfigValue
	public static String RUNEWORD_DETAILS_MENU = "Runeword Details";

	@ConfigValue
	public static String NORMAL_RARITY = "Item";

	@ConfigValue
	public static String ITEM_LEVEL = "Item level";

	@ConfigValue
	public static String ITEM_RESTRICTION = "Restrictions";

	@ConfigValue
	public static String HEALTH = "Health";

	@ConfigValue
	public static String MANA = "Mana";

	@ConfigValue
	public static String ITEM_DAMAGE = "Item damage";

	@ConfigValue
	public static String CANNOT_USE_ITEM_CONFIGURATION_REASON = "You are not trained to use this item";

	@ConfigValue
	public static String CANNOT_USE_ITEM_LEVEL_REASON = "The item requires higher character level";

	@ConfigValue
	public static String CANNOT_USE_ITEM_LORE_REASON = "You are not trained to use this item";

	@ConfigValue
	public static String NO_PARTYMEMBERS = "No nearby partymembers!";

	@ConfigValue
	public static String PLAYER_CHOOSED_SKILLTREE_PATH_GLOBAL_MESSAGE_CONTENT = "Player %player%, character %character% choosed %path%";

	@ConfigValue
	public static String SKILL_TYPE_TARGETTED = "Targetted";

	@ConfigValue
	public static String SKILL_TYPE_ACTIVE = "Active";

	@ConfigValue
	public static String SKILL_TYPE_PASSIVE = "Passive";

	@ConfigValue
	public static String MIN_PLAYER_LEVEL = "Min. Player Level";

	@ConfigValue
	public static String MAX_SKILL_LEVEL = "Max. Skill Level";

	@ConfigValue
	public static String CONFIRM_SKILL_SELECTION_BUTTON = "Confirm change";

	@ConfigValue
	public static String SKILLTREE = "SkillTree";

	@ConfigValue
	public static String CANCELLED = "Cancelled";

	@ConfigValue
	public static String INTERACTIVE_SKILLTREE_MOD_FAST = "Fast";

	@ConfigValue
	public static String INTERACTIVE_SKILLTREE_MOD_DETAILS = "Details";

	@ConfigValue
	public static String SKILL_LEVEL = "Skill Level";

	@ConfigValue
	public static String SKILL_VALUE_STARTS_AT = "Value";

	@ConfigValue
	public static String SKILL_VALUE_PER_LEVEL = "Value per skill level";

	@ConfigValue
	public static String UNKNOWN_ATTRIBUTE = "Unknown attribute %attribute%";

	@ConfigValue
	public static String ALREADY_HAS_THIS_CLASS = "Cannot choose a class, which you already have";

	@ConfigValue
	public static String ALREADY_HAS_THIS_RACE = "Cannot choose a race, which you already have";

	@ConfigValue
	public static String UNKNOWN_SKILL = "Unknown Skill %skill%";

	@ConfigValue
	public static String RACE_CANNOT_BECOME_CLASS =	"&CRace %race cannot become %class%";

	@ConfigValue
	public static String RACE_NOT_SELECTED = "&CYou have to select race before class";

	@ConfigValue
	public static String ITEM_EFFECTS_SECTION = "&9Effects";

	@ConfigValue
	public static String ITEM_RARITY_SECTION = "&8Rarity: ";

	@ConfigValue
	public static String ITEM_DAMAGE_SECTION = "&8Damage: ";

	@ConfigValue
	public static String ITEM_LEVEL_SECTION = "&8Level: ";

	@ConfigValue
	public static String ITEM_SOCKETS_SECTION = "&6Sockets";

	@ConfigValue
	public static String ITEM_ATTRIBUTES_SECTIO = "&6Attributes";

	@ConfigValue
	public static Map<String, String> SOCKET_TYPES = new HashMap<String, String>() {{
		put(SocketTypes.ANY.getId(), "&8Any");
		put(SocketTypes.GEM.getId(), "&8Gem");
		put(SocketTypes.JEWEL.getId(), "&8Jewel");
		put(SocketTypes.RUNE.getId(), "&8Rune");
	}};

	@ConfigValue
	public static String SOCKET_EMPTY = "&8 < empty %s socket> ";
}
