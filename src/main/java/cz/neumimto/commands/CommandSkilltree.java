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

import cz.neumimto.GroupService;
import cz.neumimto.ResourceLoader;
import cz.neumimto.configuration.Localization;
import cz.neumimto.gui.Gui;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.skills.SkillInfo;
import cz.neumimto.skills.StartingPoint;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by ja on 31.8.2015.
 */
@ResourceLoader.Command
public class CommandSkilltree extends CommandBase {
    @Inject
    private GroupService groupService;

    @Inject
    private CharacterService characterService;

    public CommandSkilltree() {

    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        if (commandSource instanceof Player) {
            String[] args = s.split(" ");
            NClass nClass = null;
            SkillInfo skillInfo = null;
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equalsIgnoreCase("class")) {
                    nClass = groupService.getNClass(args[i + 1]);
                    if (nClass == NClass.Default) {
                        commandSource.sendMessage(Texts.of(Localization.NON_EXISTING_GROUP));
                        return CommandResult.empty();
                    }
                    if (args[i].equalsIgnoreCase("skill")) {
                        skillInfo = nClass.getSkillTree().getSkills().get(args[i + 1]);
                        if (skillInfo == SkillInfo.EMPTY) {
                            commandSource.sendMessage(Texts.of(Localization.SKILL_DOES_NOT_EXIST));
                            return CommandResult.empty();
                        }
                    }
                }
            }
            Player player = (Player) commandSource;
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (character.isStub()) {
                player.sendMessage(Texts.of(Localization.CHARACTER_IS_REQUIRED));
            }
            if (nClass == null) {
                nClass = character.getPrimaryClass().getnClass();
            }
            if (skillInfo == null) {
                skillInfo = nClass.getSkillTree().getSkills().get(StartingPoint.name);
            }
            Gui.moveSkillTreeMenu(character, nClass.getSkillTree(), character.getCharacterBase().getSkills(), skillInfo);
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
