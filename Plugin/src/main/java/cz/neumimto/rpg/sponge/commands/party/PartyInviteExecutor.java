package cz.neumimto.rpg.sponge.commands.party;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class PartyInviteExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<Player>getOne(TextHelper.parse("player")).ifPresent(o -> {
            NtRpgPlugin.GlobalScope.partyService.sendPartyInvite(
                    NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src).getParty(),
                    NtRpgPlugin.GlobalScope.characterService.getCharacter(o));
        });
        return CommandResult.success();
    }
}
