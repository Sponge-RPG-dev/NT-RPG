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

import cz.neumimto.ResourceLoader;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.*;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;


@ResourceLoader.Command
public class CommandAdmin extends CommandBase {

    @Inject
    private SkillService skillService;

    @Inject
    private CharacterService characterService;

    @Inject
    private Logger logger;

    public CommandAdmin() {
        setDescription("Bypasses many plugin restrictions, allows you to force execute skill, set character properties..., bad use of this command may breaks plugin mechanics or cause exceptions.");
        setPermission("ntrpg.superadmin");
        setUsage("nadmin");
        addAlias("nadmin");
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        String[] a = s.split(" ");
        if (a[0].equalsIgnoreCase("use")) {
            if (!(commandSource instanceof Player)) {
                logger.warn("Can't be executed from console");
                return CommandResult.empty();
            }
            if (a[1].equalsIgnoreCase("skill")) {
                if (a.length < 2) {
                     commandSource.sendMessage(Texts.of("/nadmin use skill {skillname} [level]"));
                    return CommandResult.empty();
                }
                ISkill skill = skillService.getSkill(a[2]);
                SkillSettings defaultSkillSettings = skill.getDefaultSkillSettings();
                IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
                if (character.isStub())
                    throw new RuntimeException("Character is required even for an admin.");
                int level = 1;
                if (a.length == 4)
                    level = Integer.parseInt(a[3]);
                if (skill instanceof ActiveSkill) {
                    ExtendedSkillInfo extendedSkillInfo = new ExtendedSkillInfo();
                    extendedSkillInfo.setLevel(level);
                    SkillInfo skillInfo = new SkillInfo(skill.getName());
                    skillInfo.setSkillSettings(defaultSkillSettings);
                    extendedSkillInfo.setSkillInfo(skillInfo);
                    extendedSkillInfo.setSkill(skill);
                    ActiveSkill askill = (ActiveSkill) skill;
                    askill.cast(character, extendedSkillInfo);
                }
            }
        } else if (a[0].equalsIgnoreCase("set")) {

        } else if (a[0].equalsIgnoreCase("delete")) {

        }
        return CommandResult.success();
    }
}
