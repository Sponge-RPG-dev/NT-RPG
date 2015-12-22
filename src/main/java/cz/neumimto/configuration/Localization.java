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
 * Created by NeumimTo on 31.1.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "Localization.conf")
public class Localization {


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
}
