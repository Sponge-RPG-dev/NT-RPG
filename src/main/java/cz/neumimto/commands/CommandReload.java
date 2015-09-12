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
