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
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.SkillTreeViewModel;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.skills.SkillTree;
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
	public CommandResult process(CommandSource commandSource, String s) {
		Player p =(Player) commandSource;
		IActiveCharacter character = characterService.getCharacter(p);
		if (character.isStub()) {
			p.sendMessage(TextHelper.parse(Localization.CHARACTER_IS_REQUIRED));
			return CommandResult.empty();
		}
		ConfigClass configClass;
		if ("".equals(s.trim()) ) {
			configClass = character.getPrimaryClass().getConfigClass();
		} else {
			configClass = groupService.getNClass(s);
		}
		if (configClass == null || configClass == ConfigClass.Default) {
			Gui.sendMessage(character, Localization.NON_EXISTING_GROUP);
			return CommandResult.builder().build();
		}
		SkillTree skillTree = configClass.getSkillTree();
		for (SkillTreeViewModel treeViewModel : character.getSkillTreeViewLocation().values()) {
			treeViewModel.setCurrent(false);
		}
		if (character.getSkillTreeViewLocation().get(skillTree.getId()) == null){
			SkillTreeViewModel skillTreeViewModel = new SkillTreeViewModel();
			character.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
			skillTreeViewModel.setSkillTree(skillTree);
		} else {
			character.getSkillTreeViewLocation().get(skillTree.getId()).setCurrent(true);
		}
		Gui.openSkillTreeMenu(character);
		return CommandResult.success();
	}
}
