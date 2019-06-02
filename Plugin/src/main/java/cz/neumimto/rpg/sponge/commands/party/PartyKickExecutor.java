package cz.neumimto.rpg.sponge.commands.party;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class PartyKickExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<IActiveCharacter>getOne(TextHelper.parse("player")).ifPresent(o -> {
            NtRpgPlugin.GlobalScope.partyService.kickCharacterFromParty(NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src).getParty(), o);
        });
        return CommandResult.success();
    }
}
