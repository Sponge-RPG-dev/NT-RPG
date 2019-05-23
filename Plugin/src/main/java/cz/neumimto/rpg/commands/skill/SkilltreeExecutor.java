package cz.neumimto.rpg.commands.skill;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.SkillTreeViewModel;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SkilltreeExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src instanceof Player) {
			Player p = (Player) src;
			IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(p);
			if (character.isStub()) {
				p.sendMessage(Localizations.CHARACTER_IS_REQUIRED.toText());
				return CommandResult.empty();
			}
			Optional<ClassDefinition> aClass = args.getOne("class");
			if (!aClass.isPresent()) {
				PlayerClassData primaryClass = character.getPrimaryClass();
				if (primaryClass == null) {
					Gui.sendMessage(character, Localizations.NO_PRIMARY_CLASS, Arg.EMPTY);
					return CommandResult.builder().build();
				}
				aClass = Optional.of(primaryClass.getClassDefinition());
			}
			ClassDefinition configClass = aClass.get();

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
		} else {
			throw new CommandException(Text.of("Only for players"));
		}
	}
}
