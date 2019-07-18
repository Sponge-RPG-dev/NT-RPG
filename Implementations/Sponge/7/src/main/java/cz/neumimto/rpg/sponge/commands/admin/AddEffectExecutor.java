package cz.neumimto.rpg.sponge.commands.admin;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.commands.CommandProcessingException;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AddEffectExecutor implements CommandExecutor {

    @Inject
    private AdminCommandFacade adminCommandFacade;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        IGlobalEffect effect = args.<IGlobalEffect>getOne("effect").get();
        Long duration = args.<Long>getOne("duration").get();
        String data = args.<String>getOne("data").get();

        SpongeCharacterService characterService = Rpg.get().getCharacterService();
        IActiveCharacter character = characterService.getCharacter(player.getUniqueId());

        try {
            adminCommandFacade.commandAddEffectToPlayer(data,effect, duration, character);
            return CommandResult.success();
        } catch (CommandProcessingException e) {
            LiteralText text = Text.of(e.getMessage());
            src.sendMessage(text);
            throw new CommandException(text);
        }
    }
}
