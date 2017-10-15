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

package cz.neumimto.rpg.commands;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.CommandPermissions;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


/**
 * Created by NeumimTo on 23.7.2015.
 */
@ResourceLoader.Command
public class SkillExecuteCommand extends CommandBase {

	@Inject
	private SkillService skillService;

	@Inject
	private CharacterService characterService;

	public SkillExecuteCommand() {
		setPermission(CommandPermissions.CHARACTER_EXECUTE_SKILL_PERMISSION);
		setDescription(CommandLocalization.COMMAND_SKILL_DESC);
		alias.add("skill");
	}

	@Override
	public CommandResult process(CommandSource commandSource, String s) throws CommandException {
		IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
		if (character.isStub()) {
			commandSource.sendMessage(Text.of(Localization.CHARACTER_IS_REQUIRED));
			return CommandResult.empty();
		}

		ExtendedSkillInfo info = character.getSkillInfo(s);
		if (info == ExtendedSkillInfo.Empty || info == null) {
			commandSource.sendMessage(Text.of(Localization.CHARACTER_DOES_NOT_HAVE_SKILL));
		}
		SkillResult sk = skillService.executeSkill(character, info);
		switch (sk) {
			case ON_COOLDOWN:
				Gui.sendMessage(character, Localization.ON_COOLDOWN);
				break;
			case NO_MANA:
				Gui.sendMessage(character, Localization.NO_MANA);
				break;
			case NO_HP:
				Gui.sendMessage(character, Localization.NO_HP);
				break;
			case CASTER_SILENCED:
				Gui.sendMessage(character, Localization.PLAYER_IS_SILENCED);
				break;
			case NO_TARGET:
				Gui.sendMessage(character, Localization.NO_TARGET);
		}
		return CommandResult.empty();
	}
}
