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
    public static String SKILL_UPGRADED_BROADCAST = "%1 has upgraded skill %2";

    @ConfigValue
    public static String SKILL_LEARNED_BROADCAST = "%1 has learned %2";

    @ConfigValue
    public static String SKILL_UPGRADED = "You've upgraded skill %1 to level %2";

    @ConfigValue
    public static String SKILL_LEARNED = "You've learned skill %1";

    @ConfigValue
    public static String WEAPON_EQUIPED = "You have equiped weapon %1";

    @ConfigValue
    public static String WEAPON_CANT_BE_EQUIPED = "You can't use %1";

    @ConfigValue
    public static String CHARACTER_CREATION = "";

    @ConfigValue
    public static String SPEED_BOOST_APPLY = "speed boost applied";

    @ConfigValue
    public static String SPEED_BOOST_EXPIRE = "speed boost expired";

    @ConfigValue
    public static String LOADING_CHARACTERS = "Preloading characters, please wait";

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
    public static String PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE = "Player %1 has learned skill %2";

    @ConfigValue
    public static String PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE = "Player %1 has upgraded skill %2 to level %3";

    @ConfigValue
    public static String PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE = "Player %1 has has refunded skill %2";

    @ConfigValue
    public static String PLAYER_IS_SILENCED = "You can't use this skill, you are silenced.";

    @ConfigValue
    public static String SKILL_DOES_NOT_EXIST = "This skill does not exists";

    @ConfigValue
    public static String CHARACTER_DOES_NOT_HAVE_SKILL = "You dont have this skill";

    @ConfigValue
    public static String ON_COOLDOWN = "%1 has %2 seconds of cooldown";

    @ConfigValue
    public static String CANT_USE_PASSIVE_SKILL = "You can't use passive skill";

    @ConfigValue
    public static String SKILL_SETTINGS_LORESECTION_NAME = "Progression";

    @ConfigValue
    public static String LORESECTION_MAX_PLAYER_LEVEL = "Required level";

    @ConfigValue
    public static String LORESECTION_MAX_SKILL_LEVEL = "Max. skill level";

    @ConfigValue
    public static String LORESECTION_CONFCLICTS = "Conflicting";

    @ConfigValue
    public static String LORESECTION_SOFT_DEPENDS = "Partially depending";

    @ConfigValue
    public static String LORESECTION_HARD_DEPENDS = "Depending";

    @ConfigValue
    public static String ALREADY_IN_PARTY = "You are already in party";

    @ConfigValue
    public static String PARTY_CREATED = "You've created a new party, use /nparty invite {player} to get a new teammembers";

    @ConfigValue
    public static String PLAYER_MSG_ON_JOIN_PARTY = "You have joined party";

    @ConfigValue
    public static String PARTY_MSG_ON_PLAYER_JOIN = "%1 has joined your party";

    @ConfigValue
    public static String NON_EXISTING_CHARACTER = "Selected character does not exist";

    @ConfigValue
    public static String PLAYER_CHOOSED_CLASS = "You've chosen class %1";

    @ConfigValue
    public static String PLAYER_CHOOSED_RACE = "You've chosen race 1%";

    @ConfigValue
    public static String ALREADY_CUURENT_CHARACTER = "This character has been already activated";

    @ConfigValue
    public static String NO_TARGET = "No target";

    @ConfigValue
    public static String CURRENT_CHARACTER = "Selected character: %1";

    @ConfigValue
    public static String CHARACTER_GAINED_POINTS = "You've gained %1 skillpoints and 2% attribute points";

    @ConfigValue
    public static String NON_EXISTING_GLOBAL_EFFECT = "The effect with given name does not exists";

    @ConfigValue
    public static String NO_ITEM_IN_HAND = "An item in hand is required for this action";

    @ConfigValue
    public static String NO_ACCESS_TO_SKILL = "You dont have access to this skill.";

    @ConfigValue
    public static String CAST_SKILL_ON_RIGHTLICK = "Casts skill %1 on rightclick";

    @ConfigValue
    public static String EMPTY_HAND_REQUIRED = "An empty hand is required";

    @ConfigValue
    public static String CAST_SKILl_ON_LEFTCLICK = "Casts skill %1 on rightclick";

    @ConfigValue
    public static String ITEM_SKILLBIND_FOOTER = "For casting bounded skills you must have access to the skills via command /skill";

    @ConfigValue
    public static String RUNE = "Rune";

    @ConfigValue
    public static String NO_SKILLPOINTS = "You dont have any skillpoints.";

    @ConfigValue
    public static String NOT_LEARNED_SKILL = "You have to learn the skill first before spending skillpoints";

    @ConfigValue
    public static String SKILL_REQUIRES_HIGHER_LEVEL = "Upgrading the skill %1 requires at least level %2";

    @ConfigValue
    public static String SKILL_IS_ON_MAX_LEVEL = "The skill %1 is on its maximal level.";

    @ConfigValue
    public static String SKILL_NOT_IN_A_TREE = "The skill %1 is not accessible from your skill tree";

    @ConfigValue
    public static String MISSING_SKILL_DEPENDENCIES = "The skill %1 requires [%2] and at least one of [%3]";

    @ConfigValue
    public static String SKILL_ALREADY_LEARNED = "You've already learned this skill";

    @ConfigValue
    public static String SKILLBIND = "Bind";

    @ConfigValue
    public static String RUNE_FOOTER = "Can be inserted into socketed items";

    @ConfigValue
    public static String SOCKET = "Socketed Item";

    @ConfigValue
    public static String CLASS_INVENTORYMENU_FOOTER = "Rightclick for more info, Leftclick for skilltree";

    @ConfigValue
    public static String UNKNOWN_RUNE_NAME = "You can work with unknown types of runes. The rune has been probably removed";

    @ConfigValue
    public static String RUNEWORD = "Runeword";

    @ConfigValue
    public static String RESTRICTED_CLASSES = "Restricted classes";

    @ConfigValue
    public static String ALLOWED_CLASSES = "Allowed classes";

    @ConfigValue
    public static String MIN_LEVEL = "Min. level";

    @ConfigValue
    public static String CHARM_INFO = "Keep in your hotbar to gain bonus";

    @ConfigValue
    public static String CHARM = "Charm";

    @ConfigValue
    public static String RACE_AND_CLASS_CONFLICT = "Race %1 can't become %2";

    @ConfigValue
    public static String ARGUMENT_MUST_BE_POSITIVE_INT = "Argument must be positive integer";

    @ConfigValue
    public static String RACE_IS_REQUIRED = "You have to choose character's race.";

    @ConfigValue
    public static String SOCKET_HELP = "Interact with weapon to imbue the rune";

    @ConfigValue
    public static String SOCKET_CANCELLED = "Socketing cancelled";

    @ConfigValue
    public static String MAX_LEVEL = "Max. Level";

    @ConfigValue
    public static String HELMETS = "Helmets";

    @ConfigValue
    public static String CHESTPLATES = "Chestplates";

    @ConfigValue
    public static String LEGGINGS = "Leggings";

    @ConfigValue
    public static String BOOTS = "Boots";

    @ConfigValue
    public static String ALLOWED_ARMOR = "Armor";

    @ConfigValue
    public static String ATTRIBUTE_POINTS_PER_LEVEL = "APPL";

    @ConfigValue
    public static String TOTAL_EXP = "Total exp. amount";

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
    public static String SKILL_CONFLICTS = "Skill %1 conflicts with %2";

    @ConfigValue
    public static String RUNE_LIST = "Runes";

    @ConfigValue
    public static String RUNEWORD_ITEMS_MENU_TOOLTIP = "List of items, which can be used for crafting %1";

    @ConfigValue
    public static String RUNEWORD_ITEMS_MENU = "Allowed Items";

    @ConfigValue
    public static String RUNEWORD_ALLOWED_GROUPS_MENU = "Allowed Races or Classes";

    @ConfigValue
    public static String RUNEWORD_ALLOWED_GROUPS_MENU_TOOLTIP = "At least one of them character has to have";

    @ConfigValue
    public static String RUNEWORD_REQUIRED_GROUPS_MENU = "Required Races and Classes";

    @ConfigValue
    public static String RUNEWORD_REQUIRED_GROUPS_MENU_TOOLTIP = "All of them has to character have";

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
    public static String SKILL_DRAIN_DESC;
}
