package cz.neumimto.rpg.commands.character;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CharacterAttributeExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		args.<ICharacterAttribute>getOne(Text.of("attribute")).ifPresent(iCharacterAttribute -> {
			Integer i = args.<Integer>getOne("amount").orElse(1);
			IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
			NtRpgPlugin.GlobalScope.characterService.addAttribute(character, iCharacterAttribute, i);
			NtRpgPlugin.GlobalScope.characterService.putInSaveQueue(character.getCharacterBase());
		});
		return CommandResult.empty();
	}
}
