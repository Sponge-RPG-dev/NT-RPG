package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.ActionResult;
import cz.neumimto.rpg.sponge.configuration.Localizations;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CharacterChooseClassExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ClassDefinition configClass = args.<ClassDefinition>getOne("class").get();

        if (!src.hasPermission("ntrpg.class." + configClass.getName().toLowerCase())) {
            src.sendMessage(Localizations.NO_PERMISSIONS.toText());
            return CommandResult.empty();
        }
        if (!(src instanceof Player)) {
            throw new IllegalStateException("Cannot be run as a console");
        }
        Player player = (Player) src;
        IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
        if (character.isStub()) {
            player.sendMessage(Localizations.CHARACTER_IS_REQUIRED.toText());
            return CommandResult.empty();
        }
        ActionResult result = NtRpgPlugin.GlobalScope.characterService.canGainClass(character, configClass);
        if (result.isOk()) {
            NtRpgPlugin.GlobalScope.characterService.addNewClass(character, configClass);
        } else {
            src.sendMessage(Text.of(result.getErrorMesage()));
        }
        return CommandResult.success();
    }
}
