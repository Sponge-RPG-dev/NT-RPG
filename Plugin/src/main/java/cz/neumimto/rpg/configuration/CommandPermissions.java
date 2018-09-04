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
 * Created by NeumimTo on 23.7.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "Permissions.conf")
public class CommandPermissions {

	@ConfigValue
	public static String CHOOSEGROUP_ALIAS = "choose";

	@ConfigValue
	public static String COMMAND_CHOOSE_ACCESS = "*";

	@ConfigValue
	public static String COMMANDINFO_PERMS = "*";

	@ConfigValue
	public static String COMMANDINFO_ALIAS = "show";

	@ConfigValue
	public static String CHARACTER_INFO_ALIAS = "character";

	@ConfigValue
	public static String COMMAND_CREATE_ALIAS = "create";

	@ConfigValue
	public static String CANT_CHOOSE_RACE = ".";

	@ConfigValue
	public static String CHARACTER_EXECUTE_SKILL_PERMISSION = "*";

	@ConfigValue
	public static String COMMAND_PARTY_ALIAS = "party";

	@ConfigValue
	public static String SELECT_CLASS = "ntrpg.select.class.";

	@ConfigValue
	public static String SELECT_RACE = "ntrpg.select.race.";

	@ConfigValue
	public static String ATTRIBUTES = "ntrpg.manage.attributes";

	@ConfigValue
	public static String PARTY_CREATE = "ntrpg.party.create";

	@ConfigValue
	public static String SHOW_RUNEWORD_LIST = "ntrpg.runewords.showlist";

	@ConfigValue
	public static String SWOW_RUNEWORD_COMBINATION = "ntrpg.runeword.showcombination";

	@ConfigValue
	public static String COMMAND_SET_HEALTHSCALE = "hs";
}
