package cz.neumimto.configuration;

/**
 * Created by NeumimTo on 26.12.2014.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "Settings.conf")
public class PluginConfig {

    @ConfigValue
    public static boolean CREATE_FIRST_CHAR_AFTER_LOGIN = true;

    @ConfigValue
    public static boolean REMOVE_PLAYERDATA_AFTER_PERMABAN = false;

    @ConfigValue
    public static boolean DEBUG = true;

    @ConfigValue
    public static short SKILLPOINTS_ON_START = 1;

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
    public static short ATTRIBUTEPOINTS_ON_START = 1;

    @ConfigValue
    public static int PLAYER_MAX_CHARS = 5;

    @ConfigValue
    public static boolean CAN_REFUND_SKILL = true;

    @ConfigValue
    public static String JJS_ARGS = "-ot -strict";

    @ConfigValue
    public static long MANA_REGENERATION_RATE = 1000;

    @ConfigValue
    public static String LORE_EFFECT_COLOR = "$b";

    @ConfigValue
    public static boolean ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS = true;
}
