package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CharacterSwitchExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		args.<String>getOne("name").ifPresent(s -> {
			Player player = (Player) src;
			IActiveCharacter current = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
			if (current != null && current.getName().equalsIgnoreCase(s)) {
				player.sendMessage(Localizations.ALREADY_CUURENT_CHARACTER.toText());
				return;
			}
			CompletableFuture.runAsync(() -> {
				List<CharacterBase> playersCharacters = NtRpgPlugin.GlobalScope.characterService.getPlayersCharacters(player.getUniqueId());
				boolean b = false;
				for (CharacterBase playersCharacter : playersCharacters) {
					if (playersCharacter.getName().equalsIgnoreCase(s)) {
						ActiveCharacter character =
								NtRpgPlugin.GlobalScope.characterService.createActiveCharacter(player.getUniqueId(), playersCharacter);

						Sponge.getScheduler()
								.createTaskBuilder()
								.name("SetCharacterCallback" + player.getUniqueId())
								.execute(() -> {
									NtRpgPlugin.GlobalScope.characterService.setActiveCharacter(player.getUniqueId(), character);
									NtRpgPlugin.GlobalScope.characterService.invalidateCaches(character);
									NtRpgPlugin.GlobalScope.characterService.assignPlayerToCharacter(player);
								})
								.submit(NtRpgPlugin.GlobalScope.plugin);

						b = true;
						//Update characterbase#updated, so next time plazer logs it it will autoselect this character,
						// even if it was never updated afterwards
						NtRpgPlugin.GlobalScope.characterService.save(playersCharacter);
						break;
					}
				}
				if (!b) {
					player.sendMessage(Localizations.NON_EXISTING_CHARACTER.toText());
				}
			}, NtRpgPlugin.asyncExecutor);
		});
		return CommandResult.success();
	}
}
