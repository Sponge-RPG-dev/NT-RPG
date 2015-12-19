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

package cz.neumimto.configuration;

/**
 * Created by NeumimTo on 11.2.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "CommandLocalization.conf")
public class CommandLocalization {

    @ConfigValue(name = "playerinfo.help")
    public static String PLAYERINFO_HELP = "Shows info about player";

    @ConfigValue(name = "playerinfo.desc")
    public static String PLAYERINFO_DESC = "Shows info about player";

    @ConfigValue(name = "choosegroup.usage")
    public static String COMMAND_CHOOSEGROUP_USAGE =  "/choose {class|race} [name]";


    @ConfigValue(name = "playerinfo.usage")
    public static String PLAYERINFO_USAGE = "/info {character|player|race(s)|guild(s)|class(es)|runes} [name]";

    @ConfigValue
    public static String COMMAND_CHOOSE_DESC = "Allows you to choose a class and a race for your character.";

    @ConfigValue
    public static String COMMAND_CREATE_USAGE = "/create character [name]";

    @ConfigValue
    public static String COMMAND_CREATE_DESCRIPTION = "Allows you to create a new character";

    @ConfigValue
    public static String CHARACTER_CREATED = "You've created a new character named %1";

    @ConfigValue
    public static String COMMAND_SKILL_DESC = "Executes a skill";

    @ConfigValue
    public static String COMMAND_PARTY_USAGE = "/nparty {leave,leader,kick,invite} [name]";

    @ConfigValue
    public static String COMMAND_PARTY_DESCRIPTION = "manages party";
}
