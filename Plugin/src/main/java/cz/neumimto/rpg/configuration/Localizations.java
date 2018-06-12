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

import cz.neumimto.core.localization.Localization;
import cz.neumimto.rpg.inventory.sockets.SocketTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 31.1.2015.
 */
@Localization("rpg.main")
public class Localizations {

    public static String NO_MANA = "Not enough mana";

    public static String NO_HP = "Not enough hp";

    public static String SKILL_UPGRADED = "You've upgraded skill %skill% to level %level%";

    public static String SKILL_LEARNED = "You've learned skill %skill%";

    public static String SPEED_BOOST_APPLY = "speed boost applied";

    public static String SPEED_BOOST_EXPIRE = "speed boost expired";

    public static String PLAYER_IS_OFFLINE_MSG = "The player is offline";

    public static String REACHED_CHARACTER_LIMIT = "You've reached character limit";

    public static String CHARACTER_EXISTS = "Character with same name already exists.";

    public static String NON_EXISTING_GROUP = "This group does not exists";

    public static String NO_PERMISSIONS = "You can't do that";

    public static String CHARACTER_IS_REQUIRED = "You need to create a character for this action";

    public static String PLAYER_CANT_CHANGE_RACE = "You can't change race";

    public static String PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE = "Player %player% has learned skill %skill%";

    public static String PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE = "Player %player% has upgraded skill %skill% to level %skill%";

    public static String PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE = "Player %player% has has refunded skill %skill%";

    public static String PLAYER_IS_SILENCED = "You can't use this skill, you are silenced.";

    public static String CHARACTER_DOES_NOT_HAVE_SKILL = "You dont have this skill";

    public static String ON_COOLDOWN = "%skill% has %time% seconds of cooldown";

    public static String CANT_USE_PASSIVE_SKILL = "You can't use passive skill";

    public static String ALREADY_IN_PARTY = "You are already in party";

    public static String PARTY_CREATED = "You've created a new party, use /party invite {player} to get a new teammembers";

    public static String PLAYER_MSG_ON_JOIN_PARTY = "You have joined party";

    public static String PARTY_MSG_ON_PLAYER_JOIN = "%s has joined your party";

    public static String NON_EXISTING_CHARACTER = "Selected character does not exist";

    public static String PLAYER_CHOOSED_CLASS = "You've chosen class %class%";

    public static String UNKNOWN_CLASS = "Class %class% does not exist";

    public static String PLAYER_CHOOSED_RACE = "You've chosen race %race%";

    public static String UNKNOWN_RACE = "Race %race% does not exist";

    public static String ALREADY_CUURENT_CHARACTER = "This character has been already activated";

    public static String NO_TARGET = "No target";

    public static String CURRENT_CHARACTER = "Selected character: %character%";

    public static String CHARACTER_GAINED_POINTS = "You've gained %skillpoints% skillpoints and %attributes% attribute points";

    public static String NO_ITEM_IN_HAND = "An item in hand is required for this action";

    public static String NO_ACCESS_TO_SKILL = "You dont have access to this skill.";

    public static String CAST_SKILL_ON_RIGHTLICK = "&8Casts skill &2%skill%&8  on rightclick";

    public static String EMPTY_HAND_REQUIRED = "An empty hand is required";

    public static String CAST_SKILl_ON_LEFTCLICK = "&8Casts skill &2%skill%&8  on rightclick";

    public static String ITEM_SKILLBIND_FOOTER = "For casting bounded skills you must have access :n to the skills via command /skill";

    public static String RUNE = "Rune";

    public static String NO_SKILLPOINTS = "You dont have any skillpoints.";

    public static String NOT_LEARNED_SKILL = "You have to learn the skill first before spending skillpoints";

    public static String SKILL_REQUIRES_HIGHER_LEVEL = "Upgrading the skill %skill% requires at least level %level%";

    public static String SKILL_IS_ON_MAX_LEVEL = "The skill %skill% is on its maximal level - %level%.";

    public static String SKILL_NOT_IN_A_TREE = "The skill %skill% is not accessible from your skill tree";

    public static String MISSING_SKILL_DEPENDENCIES = "The skill %skill% requires [%hard%] and at least one of [%soft%]";

    public static String SKILL_ALREADY_LEARNED = "You've already learned the skill %skill% ";

    public static String SKILLBIND = "Bind";

    public static String RUNE_FOOTER = "Can be inserted into socketed items";

    public static String SOCKET = "Socketed Item";

    public static String UNKNOWN_RUNE_NAME = "You can work with unknown types of runes. The rune has been probably removed";

    public static String RUNEWORD = "Runeword";

    public static String CHARM_INFO = "Keep in your hotbar to gain bonus";

    public static String CHARM = "Charm";

    public static String SOCKET_HELP = "Use anvil to imbue socket";

    public static String SOCKET_CANCELLED = "Socketing cancelled";

    public static String WEAPONS = "Weapons";

    public static String ARMOR = "Armor";

    public static String WEAPONS_MENU_HELP = "Displays list of allowed weapons and damage values";

    public static String ARMOR_MENU_HELP = "Displays list of allowed armor";

    public static String ATTRIBUTES = "Attributes";

    public static String BACK = "Back";

    public static String INITIAL_VALUE = "Attributes at start";

    public static String CONFIRM = "Confirm";

    public static String SKILL_CONFLICTS = "Skill %skill% conflicts with %conflict%";

    public static String RUNE_LIST = "Runes";

    public static String RUNEWORD_ITEMS_MENU_TOOLTIP = "List of items, which can be used for crafting %runeword%";

    public static String RUNEWORD_ITEMS_MENU = "Allowed Items";

    public static String RUNEWORD_ALLOWED_GROUPS_MENU = "Allowed Races or Classes";

    public static String RUNEWORD_ALLOWED_GROUPS_MENU_TOOLTIP = "At least one of them character has to have";

    public static String RUNEWORD_BLOCKED_GROUPS_MENU = "Blocked Races and Classes";

    public static String RUNEWORD_BLOCKED_GROUPS_MENU_TOOLTIP = "Any of them has to character have";

    public static String RUNEWORD_DETAILS_MENU = "Runeword Details";

    public static String NORMAL_RARITY = "Item";

    public static String ITEM_LEVEL = "Item level";

    public static String ITEM_RESTRICTION = "Restrictions";

    public static String HEALTH = "Health";

    public static String MANA = "Mana";

    public static String ITEM_DAMAGE = "Item damage";

    public static String CANNOT_USE_ITEM_CONFIGURATION_REASON = "You are not trained to use this item";

    public static String CANNOT_USE_ITEM_LEVEL_REASON = "The item requires higher character level";

    public static String CANNOT_USE_ITEM_LORE_REASON = "You are not trained to use this item";

    public static String NO_PARTYMEMBERS = "No nearby partymembers!";

    public static String PLAYER_CHOOSED_SKILLTREE_PATH_GLOBAL_MESSAGE_CONTENT = "Player %player%, character %character% choosed %path%";

    public static String SKILL_TYPE_TARGETTED = "Targetted";

    public static String SKILL_TYPE_ACTIVE = "Active";

    public static String SKILL_TYPE_PASSIVE = "Passive";

    public static String MIN_PLAYER_LEVEL = "Min. Player Level";

    public static String MAX_SKILL_LEVEL = "Max. Skill Level";

    public static String CONFIRM_SKILL_SELECTION_BUTTON = "Confirm change";

    public static String SKILLTREE = "SkillTree";

    public static String CANCELLED = "Cancelled";

    public static String INTERACTIVE_SKILLTREE_MOD_FAST = "Fast";

    public static String INTERACTIVE_SKILLTREE_MOD_DETAILS = "Details";

    public static String SKILL_LEVEL = "Skill Level";

    public static String SKILL_VALUE_STARTS_AT = "Value";

    public static String SKILL_VALUE_PER_LEVEL = "Value per skill level";

    public static String UNKNOWN_ATTRIBUTE = "Unknown attribute %attribute%";

    public static String ALREADY_HAS_THIS_CLASS = "Cannot choose a class, which you already have";

    public static String ALREADY_HAS_THIS_RACE = "Cannot choose a race, which you already have";

    public static String UNKNOWN_SKILL = "Unknown Skill %skill%";

    public static String RACE_CANNOT_BECOME_CLASS = "&CRace %race cannot become %class%";

    public static String RACE_NOT_SELECTED = "&CYou have to select race before class";

    public static String ITEM_EFFECTS_SECTION = "&9Effects";

    public static String ITEM_RARITY_SECTION = "&8Rarity: ";

    public static String ITEM_DAMAGE_SECTION = "&8Damage: ";

    public static String ITEM_LEVEL_SECTION = "&8Level: ";

    public static String ITEM_SOCKETS_SECTION = "&6Sockets";

    public static String ITEM_ATTRIBUTES_SECTIO = "&6Attributes";

    public static String ITEM_META_TYPE_NAME = "&8Type: ";

    public static Map<String, String> SOCKET_TYPES = new HashMap<String, String>() {{
        put(SocketTypes.ANY.getId(), "&8Any");
        put(SocketTypes.GEM.getId(), "&8Gem");
        put(SocketTypes.JEWEL.getId(), "&8Jewel");
        put(SocketTypes.RUNE.getId(), "&8Rune");
    }};

    public static String SOCKET_EMPTY = "&8 < empty %s socket> ";

    public static String UNKNOWN_RARITY = "&7Unknown rarity";

    public static String ITEM_REQUIREMENTS_SECTION = "&4 Requirements";

    public static String CANNOT_USE_ITEM_GENERIC = "&4You cannot use this item";

    public static String RACES_MENU_TEXT = "&l&6[ &eRaces &6]";

    public static String CLASSES_MENU_TEXT = "&l&6[ &eClasses &6]";

    public static String LEVEL = "Level";

    public static String CHAR_DELETED_FEEDBACK = "Character deleted";

    public static String NO_CHARACTER = "You need a character for this action";

    public static String PARTY_CHAT_PREFIX = "&3Party &1>>&r ";

    public static String PLAYER_INVITED_TO_PARTY_PARTY_MSG = "%player% &8 invited to the party";

    public static String PLAYER_INVITED_TO_PARTY = "%player% &8invited you to the party";

    public static String INSUFFICIENT_LEVEL_GAP = "You need at least level %level% to be able to upgrade the skill.";

    public static String CANNOT_USE_ITEM_CONFIGURATION_REASON_OFFHAND = "&4You are not trained to dual wield with this weapon";
}
