package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.api.entity.IPropertyService;
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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class CharacterAttributeCommitExecutor implements CommandExecutor {

    @Inject
    private SpongeCharacterService characterServise;

    @Inject
    private IPropertyService propertyService;

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ISpongeCharacter character = characterServise.getCharacter((Player) src);
        Map<String, Integer> attributesTransaction = character.getAttributesTransaction();
        Map<AttributeConfig, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : attributesTransaction.entrySet()) {
            Optional<AttributeConfig> attributeById = propertyService.getAttributeById(entry.getKey());
            if (attributeById.isPresent()) {
                map.put(attributeById.get(), entry.getValue());
            }
        }
        characterCommandFacade.commandCommitAttribute(character, map);

        return CommandResult.success();
    }
}
