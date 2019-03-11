package cz.neumimto.rpg.commands.character;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CharacterShowClassExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Optional<ClassDefinition> classDefinitionOptional = args.getOne("class");
		if (classDefinitionOptional.isPresent()) {
			ClassDefinition clazz = classDefinitionOptional.get();
			if (src instanceof Player) {
				IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
				Gui.showClassInfo(character, clazz);
			} else {
				src.sendMessage(Text.of("Only for players!"));
			}
		} else {
			if (src instanceof Player) {
				IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
				if (character.getPrimaryClass() != null) {
					Gui.showClassInfo(character, character.getPrimaryClass().getClassDefinition());
				} else {
					Gui.sendClassTypes(character);
				}
			} else {
				src.sendMessage(Text.of("Only for players!"));
			}
		}
		return CommandResult.success();
	}
}
