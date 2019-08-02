package cz.neumimto.rpg.sponge.commands.admin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Singleton
public class InspectPropertyExecutor implements CommandExecutor {

    @Inject
    private PropertyService propertyService;

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private SpongeEntityService entityService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        String data = args.<String>getOne("data").get();
        PROPERTY_DETAIL.accept(src, data, player);
        return CommandResult.success();
    }

    private TriConsumer<CommandSource, String, Player> PROPERTY_DETAIL = (src, data, player) -> {
        try {
            int idByName = propertyService.getIdByName(data);
            IActiveCharacter character = characterService.getCharacter(player);
            src.sendMessage(Text.of(TextColors.GOLD, "=================="));
            src.sendMessage(Text.of(TextColors.GREEN, data));

            src.sendMessage(Text.of(TextColors.GOLD, "Value", TextColors.WHITE, "/",
                    TextColors.AQUA, "Effective Value", TextColors.WHITE, "/",
                    TextColors.GRAY, "Cap",
                    TextColors.DARK_GRAY, " .##"));

            NumberFormat formatter = new DecimalFormat("#0.00");
            src.sendMessage(Text.of(TextColors.GOLD, formatter.format(character.getProperty(idByName)), TextColors.WHITE, "/",
                    TextColors.AQUA, formatter.format(entityService.getEntityProperty(character, idByName)), TextColors.WHITE, "/",
                    TextColors.GRAY, formatter.format(propertyService.getMaxPropertyValue(idByName))));

            src.sendMessage(Text.of(TextColors.GOLD, "=================="));
            src.sendMessage(Text.of(TextColors.GRAY, "Memory/1 player: " + (character.getPrimaryProperties().length * 2 * 4) / 1024.0 + "kb"));

        } catch (Throwable t) {
            src.sendMessage(Text.of("No such property"));
        }
    };
}
