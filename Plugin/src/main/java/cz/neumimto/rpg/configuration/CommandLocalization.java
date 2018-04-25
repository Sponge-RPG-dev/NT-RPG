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
 * Created by NeumimTo on 11.2.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "CommandLocalization.conf")
public class CommandLocalization {

    @ConfigValue
	public static String COMMAND_ADMIN_ENCHANT = "&2Enchants held item with custom enchantment";
	@ConfigValue
	public static String COMMAND_ADMIN_DESC = "&2Access to administration commands";

	@ConfigValue
	public static String COMMAND_ADMIN_EXEC_SKILL_DESC = "&2Executes a skill, bypasses restrictions such as mana cost, skill access ... ";

	@ConfigValue
	public static String COMMAND_ADMIN_ENCHANT_ADD = "&2Adds enchant to the held item";

	@ConfigValue
	public static String COMMAND_ADMIN_SOCKET = "&2Inserts sockets into held item";

	@ConfigValue
	public static String COMMAND_ADMIN_RUNE = "&2Creates a rune";

	@ConfigValue
	public static String COMMAND_ADMIN_RUNEWORD = "&2Tries to insert runeword into held item";

	@ConfigValue
	public static String COMMAND_ADMIN_EXP_ADD  = "&2Adds experience to specific player and class";

	@ConfigValue
	public static String COMMAND_ADMIN_RELOAD  = "&2Reloads specific resource";

	@ConfigValue
	public static String COMMAND_BIND_DESC = "&2Binds skill to an item";

	@ConfigValue
	public static String COMMAND_MP_DESC = "&2Displays current mana";

	@ConfigValue
	public static String COMMAND_HP_DESC = "&2Health";

	@ConfigValue
	public static String COMMAND_SKILL_REFUND = "Refund a skill, refunding gives back skillpoints. Depending on server's settings class specialization may not be refunded";

	@ConfigValue
	public static String COMMAND_SKILL_UPGRADE = "Level up a skill";

	@ConfigValue
	public static String COMMAND_SKILL_LEARN = "Learn a new skill";

	@ConfigValue
	public static String COMMAND_ATTRIBUTE = "Spends attribute points to improve character's attributes";

	@ConfigValue
	public static String PLAYERINFO_HELP = "Shows info about player";

	@ConfigValue
	public static String PLAYERINFO_DESC = "Shows info about player";

	@ConfigValue
	public static String COMMAND_ADMIN_EFFECT_ADD = "Applies effect to a player";

	@ConfigValue
	public static String PLAYERINFO_USAGE = "/info {character|player|race(s)|guild(s)|class(es)|runes} [name]";

	@ConfigValue
	public static String COMMAND_CHOOSE_DESC = "Allows you to choose a class and a race for your character.";

	@ConfigValue
	public static String COMMAND_CREATE_DESCRIPTION = "Allows you to create a new character";

	@ConfigValue
	public static String CHARACTER_CREATED = "You've created a new character named %1";

	@ConfigValue
	public static String COMMAND_SKILL_DESC = "Executes a skill";

	@ConfigValue
	public static String COMMAND_PARTY_USAGE = "/party {leave|leader|kick|invite} [name]";

	@ConfigValue
	public static String COMMAND_PARTY_DESCRIPTION = "manages party";

	@ConfigValue
	public static String COMMAND_CHOOSE_USAGE = "/choose {class [name]|race [name]|skill [upgrade|learn|refund]|character [name]}";

	@ConfigValue
	public static String COMMAND_SET_HEALTHSCALE_USAGE = "/healthscale [integer]";

	@ConfigValue
	public static String COMMAND_SET_HEALTHSCALE_DESCRIPTION = "Sets healthscale for currently choosen character, persistant between restarts";

	@ConfigValue
	public static String COMMAND_CLASSES_DESC = "Displays list of avalaible classes";

	@ConfigValue
	public static String COMMAND_CLASSES_RACE = "Displays list of avalaible races";

	@ConfigValue
	public static String COMMAND_RACE_DESC = "Displays detailed informations about a race";

	@ConfigValue
	public static String COMMAND_CLASS_DESC = "Displays detailed informations about a class";

	@ConfigValue
	public static String COMMAND_ADMIN_RARITY = "Sets rarity of current item type";

	@ConfigValue
	public static String COMMAND_ADMIN_ITEM_TYPE = "&2Sets item type";

	@ConfigValue
	public static String COMMAND_CHARACTE_LIST = "&2Lists all your characters";
}
