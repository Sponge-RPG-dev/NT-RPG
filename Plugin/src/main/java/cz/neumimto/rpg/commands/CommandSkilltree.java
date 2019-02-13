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
import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.ClassService;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.SkillTreeViewModel;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.tree.SkillTree;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by ja on 31.8.2015.
 */
@ResourceLoader.Command
public class CommandSkilltree extends CommandBase {

	@Inject
	private ClassService classService;

	@Inject
	private CharacterService characterService;

	@Inject
	private SkillService skillService;

	public CommandSkilltree() {
		addAlias("skilltree");
		setUsage("skilltree");
	}

	@Override
	public CommandResult process(CommandSource commandSource, String s) {
		Player p = (Player) commandSource;
		IActiveCharacter character = characterService.getCharacter(p);
		if (character.isStub()) {
			p.sendMessage(Localizations.CHARACTER_IS_REQUIRED.toText());
			return CommandResult.empty();
		}
		ClassDefinition configClass;
		if ("".equals(s.trim())) {
			configClass = character.getPrimaryClass().getClassDefinition();
		} else {
			configClass = classService.getClassDefinitionByName(s);
		}
		if (configClass == null) {
			Gui.sendMessage(character, Localizations.NON_EXISTING_GROUP, Arg.EMPTY);
			return CommandResult.builder().build();
		}
		SkillTree skillTree = configClass.getSkillTree();
		for (SkillTreeViewModel treeViewModel : character.getSkillTreeViewLocation().values()) {
			treeViewModel.setCurrent(false);
		}
		SkillTreeViewModel skillTreeViewModel = character.getSkillTreeViewLocation().get(skillTree.getId());
		if (skillTreeViewModel == null) {
			skillTreeViewModel = new SkillTreeViewModel();
			character.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
			skillTreeViewModel.setSkillTree(skillTree);
		} else {
			skillTreeViewModel.setCurrent(true);
		}
		skillTreeViewModel.setViewedClass(configClass);
		Gui.openSkillTreeMenu(character);
		return CommandResult.success();
	}
}
