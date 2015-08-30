package cz.neumimto.configuration;

/**
 * Created by NeumimTo on 31.1.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}",filename = "Localization.conf")
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
    public static String NO_PERMISSIONS = "You dont can't do that";

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
}
