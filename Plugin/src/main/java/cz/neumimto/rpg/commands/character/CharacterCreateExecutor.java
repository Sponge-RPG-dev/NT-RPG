package cz.neumimto.rpg.commands.character;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.CompletableFuture;

public class CharacterCreateExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String a = args.<String>getOne("name").get();
		CompletableFuture.runAsync(() -> {
			Player player = (Player) src;
			CharacterService characterService = IoC.get().build(CharacterService.class);
			int i = characterService.canCreateNewCharacter(player.getUniqueId(), a);
			if (i == 1) {
				src.sendMessage(Localizations.REACHED_CHARACTER_LIMIT.toText());
			} else if (i == 2) {
				src.sendMessage(Localizations.CHARACTER_EXISTS.toText());
			} else if (i == 0) {
				CharacterBase characterBase = new CharacterBase();
				characterBase.setUuid(player.getUniqueId());
				characterBase.setName(a);
				characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);
				characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);

				characterService.createAndUpdate(characterBase);

				src.sendMessage(TextHelper.parse(CommandLocalization.CHARACTER_CREATED.replaceAll("%1", characterBase.getName())));
				Gui.sendListOfCharacters(characterService.getCharacter(player.getUniqueId()), characterBase);
			}
		}, NtRpgPlugin.asyncExecutor);
		return CommandResult.success();
	}
}
