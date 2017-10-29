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
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

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
		addAlias("skilltree");
		setUsage("skilltree");
	}

	@Override
	public CommandResult process(CommandSource commandSource, String s) throws CommandException {
		Player p =(Player) commandSource;
		IActiveCharacter character = characterService.getCharacter(p);
		Gui.openSkillTreeMenu(character, character.getPrimaryClass().getConfigClass().getSkillTree());
		return CommandResult.success();
	}
}
