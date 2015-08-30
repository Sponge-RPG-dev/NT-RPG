package cz.neumimto.configuration;

/**
 * Created by NeumimTo on 11.2.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}",filename = "CommandLocaliuation.conf")
public class CommandLocalization {


    @ConfigValue(name = "playerinfo.help")
    public static String PLAYERINFO_HELP = "Shows info about player";

    @ConfigValue(name = "playerinfo.desc")
    public static String PLAYERINFO_DESC = "Shows info about player";

    @ConfigValue(name = "choosegroup.usage")
    public static String COMMAND_CHOOSEGROUP_USAGE = "";


    @ConfigValue(name = "playerinfo.usage")
    public static String PLAYERINFO_USAGE = "/info {character|player|race(s)|guild(s)|class(es)} [name]";

    @ConfigValue
    public static String COMMAND_CHOOSE_USAGE = "/choose {class|race} [name]";

    @ConfigValue
    public static String COMMAND_CHOOSE_DESC = "Allows you to choose class and race for your character.";

    @ConfigValue
    public static String COMMAND_CREATE_USAGE = "/create character [name]";

    @ConfigValue
    public static String COMMAND_CREATE_DESCRIPTION = "Allows you to create a new character";

    @ConfigValue
    public static String CHARACTER_CREATED = "You've created a new character named %1";

    @ConfigValue
    public static String COMMAND_SKILL_DESC = "Executes a skill";
}
