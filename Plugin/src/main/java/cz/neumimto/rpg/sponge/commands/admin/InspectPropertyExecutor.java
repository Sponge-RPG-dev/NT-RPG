package cz.neumimto.rpg.sponge.commands.admin;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;
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

public class InspectPropertyExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        String data = args.<String>getOne("data").get();
        PROPERTY_DETAIL.accept(src, data, player);
        return CommandResult.success();
    }

    private TriConsumer<CommandSource, String, Player> PROPERTY_DETAIL = (src, data, player) -> {
        SpongePropertyService ps = NtRpgPlugin.GlobalScope.spongePropertyService;
        CharacterService cs = NtRpgPlugin.GlobalScope.characterService;
        ;
        EntityService es = NtRpgPlugin.GlobalScope.entityService;

        try {
            int idByName = ps.getIdByName(data);
            IActiveCharacter character = cs.getCharacter(player);
            src.sendMessage(Text.of(TextColors.GOLD, "=================="));
            src.sendMessage(Text.of(TextColors.GREEN, data));

            src.sendMessage(Text.of(TextColors.GOLD, "Value", TextColors.WHITE, "/",
                    TextColors.AQUA, "Effective Value", TextColors.WHITE, "/",
                    TextColors.GRAY, "Cap",
                    TextColors.DARK_GRAY, " .##"));

            NumberFormat formatter = new DecimalFormat("#0.00");
            src.sendMessage(Text.of(TextColors.GOLD, formatter.format(character.getProperty(idByName)), TextColors.WHITE, "/",
                    TextColors.AQUA, formatter.format(es.getEntityProperty(character, idByName)), TextColors.WHITE, "/",
                    TextColors.GRAY, formatter.format(ps.getMaxPropertyValue(idByName))));

            src.sendMessage(Text.of(TextColors.GOLD, "=================="));
            src.sendMessage(Text.of(TextColors.GRAY, "Memory/1 player: " + (character.getPrimaryProperties().length * 2 * 4) / 1024.0 + "kb"));

        } catch (Throwable t) {
            src.sendMessage(Text.of("No such property"));
        }
    };
}
