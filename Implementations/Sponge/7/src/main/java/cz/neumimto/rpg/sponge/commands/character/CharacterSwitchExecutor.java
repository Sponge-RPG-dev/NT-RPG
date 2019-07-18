package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterSwitchExecutor implements CommandExecutor {

    @Inject
    private PartyService partyService;

    @Inject
    private SpongeCharacterService characterServise;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<String>getOne("name").ifPresent(nameNext -> {
            Player player = (Player) src;
            IActiveCharacter current = characterServise.getCharacter(player);

            characterCommandFacade.commandSwitchCharacter(current, nameNext, runnable ->
                    Sponge.getScheduler()
                    .createTaskBuilder()
                    .name("SetCharacterCallback" + player.getUniqueId())
                    .execute(runnable::run)
                    .submit(NtRpgPlugin.GlobalScope.plugin));
        });
        return CommandResult.success();
    }
}
