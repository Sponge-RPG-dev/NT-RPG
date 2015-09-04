package cz.neumimto.configuration;

/**
 * Created by NeumimTo on 23.7.2015.
 */
@ConfigurationContainer(path = "{WorkingDir}", filename = "Permissions.conf")
public class CommandPermissions {

    @ConfigValue
    public static String COMMAND_CHOOSE_CLASS = "*";
    @ConfigValue
    public static String CHOOSEGROUP_ALIAS = "choose";

    @ConfigValue
    public static String COMMAND_CHOOSE_ACCESS = "*";

    @ConfigValue
    public static String COMMANDINFO_PERMS = "*";

    @ConfigValue
    public static String COMMANDINFO_ALIAS = "info";

    @ConfigValue
    public static String CHARACTER_INFO_ALIAS = "character";

    @ConfigValue
    public static String COMMAND_CREATE_ALIAS = "create";

    @ConfigValue
    public static String COMMAND_CHOOSE_RACE = "*";

    @ConfigValue
    public static String CHARACTER_EXECUTE_SKILL_PERMISSION = "*";

    @ConfigValue
    public static String COMMAND_PARTY_ALIAS = "party";
}
