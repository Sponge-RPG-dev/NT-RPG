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

import cz.neumimto.configuration.Comment;
import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;
import cz.neumimto.rpg.inventory.ItemLoreSections;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by NeumimTo on 26.12.2014.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "Settings.conf")
public class PluginConfig {

	@Comment(content = {"If you want to use another plugin, which handles mob's hp and damage set this value to true"})
	@ConfigValue
	public static boolean OVERRIDE_MOBS = false;

	@ConfigValue
	public static long COMBAT_TIME = 20000L;

	@ConfigValue
	public static boolean REMOVE_PLAYERDATA_AFTER_PERMABAN = false;

	@ConfigValue
	public static boolean DEBUG = true;

	@ConfigValue
	public static int SKILLPOINTS_ON_START = 1;

	@ConfigValue
	public static boolean PLAYER_CAN_CHANGE_RACE = true;

	@ConfigValue
	public static boolean PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR = true;

	@ConfigValue
	public static boolean SKILLGAIN_MESSAGES_AFTER_LOGIN = true;

	@ConfigValue
	public static boolean PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE = true;

	@ConfigValue
	public static boolean PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE = true;

	@ConfigValue
	public static boolean PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE = true;

	@ConfigValue
	public static int ATTRIBUTEPOINTS_ON_START = 1;

	@ConfigValue
	public static int PLAYER_MAX_CHARS = 5;

	@ConfigValue
	public static boolean CAN_REFUND_SKILL = true;

	@ConfigValue
	@Comment(content = {"Works only, if the server is using jdk, for passing these arguments with jre use -D flag"})
	public static String JJS_ARGS = "--optimistic-types=true";

	@ConfigValue
	@Comment(content = {"Time period in milliseconds"})
	public static long MANA_REGENERATION_RATE = 1000;

	@ConfigValue
	public static boolean ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS = true;

	@ConfigValue
	@Comment(content = "Works only if PLAYER_MAX_CHARS > 1.")
	public static boolean TELEPORT_PLAYER_TO_LAST_CHAR_LOCATION = true;

	@ConfigValue
	public static Set<String> ALLOWED_RUNES_ITEMTYPES = new HashSet<String>() {{
		add("minecraft:nether_star");
	}};

	@ConfigValue
	public static boolean AUTOREMOVE_NONEXISTING_RUNEWORDS = false;

	@ConfigValue
	@Comment(content = {"Enables passing arguments to skills", "eg.: /skill Fireball arg1 arg2"})
	public static boolean SKILL_COMMAND_ARGUMENTS = false;

	@ConfigValue
	@Comment(content = {"Multiplier of shared experience gain for players in a party.", "Exp=(MobExp*Mult)/partyplayers in area"})
	public static double PARTY_EXPERIENCE_MULTIPLIER = 2;

	@ConfigValue
	public static double PARTY_EXPERIENCE_SHARE_DISTANCE = 25;

	@ConfigValue
	@Comment(content = {"Value lesser than 0 means there will be no party limit. Skills or effects can override this value."})
	public static double MAX_PARTY_SIZE = -68458;


	@ConfigValue
	@Comment(content = {"If a player chooses a race and a class, where both those groups define damage value for one specific weapon, or projectile" +
			" this option specifies how the weapon damage will be calculated."+
			"1 = sum"+
			"2 = take highest value"})
	public static int WEAPON_MERGE_STRATEGY = 2;

	@ConfigValue
	@Comment(content = {"Whenever global chat message will be displayed if any player chooses a skill tree path"})
	public static boolean PLAYER_CHOOSED_SKILLTREE_SPECIALIZATIon_GLOBAL_MESSAGE;

	@ConfigValue
	@Comment(content = {"Whenever a player is able to refund skills, representing root of the path of specialization on any skilltree"})
	public static boolean PATH_NODES_SEALED = true;

	@ConfigValue
	@Comment(content = {"Whenever pressing shift(sneak) resets click combination"})
	public static boolean SHIFT_CANCELS_COMBO = false;

	@ConfigValue
	@Comment(content = {"Recognizes pressing Q key (/throwing an item out of inventory) as the click combo option 'Q'"+
			 " This action has priority over throwing item out of the inventory. Click combo may not start whit this action"})
	public static boolean ENABLED_Q;

	@ConfigValue
	@Comment(content = {"Recognizes pressing E key (/opening player inventory) as the click combo option 'E'"+
			 " This action has priority over opening players' inventory. Click combo may not start whit this action"})
	public static boolean ENABLED_E;

	@ConfigValue
	@Comment(content = {"Time interval in milliseconds, defines maximal interval between two clicks (E/Q/RMB/LMB/S)"})
	public static long CLICK_COMBO_MAX_INVERVAL_BETWEEN_ACTIONS = 1250;

	@ConfigValue
	@Comment(content = {"If true then class is validated against character's race.allowedClasses parameter "})
	public static boolean VALIDATE_RACE_DURING_CLASS_SELECTION = true;

	@ConfigValue
	public static String ITEM_LORE_EFFECT_NAME_COLOR = TextColors.BLUE.getName();

	@ConfigValue
	public static String ITEM_LORE_EFFECT_COLON_COLOR = TextColors.DARK_GRAY.getName();

	@ConfigValue
	public static String ITEM_LORE_EFFECT_VALUE_COLOR = TextColors.LIGHT_PURPLE.getName();

	@ConfigValue
	public static String ITEM_LORE_EFFECT_SECTION_COLOR = TextColors.BLUE.getName();

	@ConfigValue
	public static String ITEM_LORE_RARITY_COLOR = TextColors.DARK_GRAY.getName();


	@ConfigValue
	public static String ITEM_LORE_GROUP_MIN_LEVEL_COLOR = TextColors.DARK_PURPLE.getName();

	@ConfigValue
	public static List<String> ITEM_LORE_ORDER = Stream.of(ItemLoreSections.values()).map(ItemLoreSections::name).collect(Collectors.toList());


	@ConfigValue
	public static List<String> SKILLTREE_RELATIONS = new ArrayList<String>() {{
		add("|,minecraft:stick,|,0");
		add("/,minecraft:stick,/,0");
		add("\\\\,minecraft:stick,\\\\,0");
		add("-,minecraft:stick,-,0");
	}};

	@ConfigValue
	public static List<String> SKILLTREE_BUTTON_CONTROLLS = new ArrayList<String>() {{
		add("North,minecraft:diamond_hoe,Up,1");
		add("West,minecraft:diamond_hoe,Right,2");
		add("East,minecraft:diamond_hoe,Left,3");
		add("South,minecraft:diamond_hoe,Down,4");
	}};

	@ConfigValue
	public static List<String> ITEM_RARITY = new ArrayList<String>() {{
		add("0,Common");
		add("1,&9Rare");
		add("2,&eUnique");
		add("3,&5Legendary");
	}};

	@ConfigValue
	public static String EQUIPED_SLOT_RESOLVE_SRATEGY = "nt-rpg:slot_order";

	@ConfigValue
	@Comment(content = "1 - Drops item out of player inventory, 2 - Drops items only from hotbar, 3 - Only warning")
	public static Integer PLAYER_IS_UNABLE_TO_USE_ITEM = 1;

	@ConfigValue
	@Comment(content = "Additional inventory slots which will be recognized for accessories, may not be hotbar slots")
	public static List<Integer> ACCESSORIES_SLOTS = new ArrayList<Integer>() {{
		add(10); //todo
	}};

	@ConfigValue
	@Comment(content = "Player may put standard item into accessory slot")
	public static boolean ACCESSORIES_BLOCK_JUNK_ITEMS = true;
}
