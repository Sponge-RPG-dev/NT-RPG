package cz.neumimto.commands;

import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.ioc.Command;
import cz.neumimto.ioc.Inject;
import cz.neumimto.scripting.JSLoader;
import cz.neumimto.skills.SkillService;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by NeumimTo on 2.8.2015.
 */
@Command
public class CommandReload extends CommandBase {

    @Inject
    private JSLoader jsLoader;

    @Inject
    private SkillService skillService;

    public CommandReload() {
        setDescription("Reloads specific resources");
        setPermission("ntrpg.admin");
        setUsage("nreload");
        addAlias("nreload");
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        if (!PluginConfig.DEBUG) {
            commandSource.sendMessage(Texts.of("Reloading is allowed only in debug mode"));
            return CommandResult.success();
        }
        String[] str = s.split(" ");
        for (String a : str) {
            if (a.equalsIgnoreCase("js")) {
                jsLoader.loadSkills();
            }
            if (a.equalsIgnoreCase("skillconf")) {
                skillService.deleteConfFile();
                skillService.load();
            }
        }
        return CommandResult.success();
    }
}
