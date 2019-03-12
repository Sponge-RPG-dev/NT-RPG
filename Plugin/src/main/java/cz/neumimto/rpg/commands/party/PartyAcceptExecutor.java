package cz.neumimto.rpg.commands.party;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class PartyAcceptExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
		if (character.getPendingPartyInvite() != null) {
			NtRpgPlugin.GlobalScope.partyService.addToParty(character.getPendingPartyInvite(), character);
		}
		return CommandResult.success();
	}
}
