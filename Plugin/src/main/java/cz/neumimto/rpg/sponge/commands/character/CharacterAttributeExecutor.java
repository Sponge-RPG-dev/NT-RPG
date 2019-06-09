package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
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
        args.<AttributeConfig>getOne(Text.of("attribute")).ifPresent(iCharacterAttribute -> {
            Integer i = args.<Integer>getOne("amount").orElse(1);
            ISpongeCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
            NtRpgPlugin.GlobalScope.characterService.addAttribute(character, iCharacterAttribute, i);
            NtRpgPlugin.GlobalScope.characterService.putInSaveQueue(character.getCharacterBase());
        });
        return CommandResult.empty();
    }
}
