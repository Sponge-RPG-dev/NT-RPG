package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterAttributeExecutor implements CommandExecutor {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Inject
    private SpongeCharacterService characterService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<AttributeConfig>getOne(Text.of("attribute")).ifPresent(a -> {
            Integer amount = args.<Integer>getOne("amount").orElse(1);

            ISpongeCharacter character = characterService.getCharacter((Player) src);
            character.getAttributesTransaction().put(a.getId(), amount);
        });
        return CommandResult.empty();
    }
}
