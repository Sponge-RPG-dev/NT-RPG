package cz.neumimto.rpg.sponge.commands.elements;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 16.11.2017.
 */
public class PartyMemberCommandElement extends CommandElement {

    private final SpongeCharacterService characterService;


    public PartyMemberCommandElement(@Nullable Text key, SpongeCharacterService characterService) {
        super(key);
        this.characterService = characterService;
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String pl = args.next();
        Optional<Player> player = Sponge.getGame().getServer().getPlayer(pl);
        if (!player.isPresent()) {
            throw args.createError(TextSerializers.FORMATTING_CODE.deserialize("&CUnknown Player &C\"" + pl + "\""));
        }
        IActiveCharacter character = characterService.getCharacter(player.get());
        IActiveCharacter pleader = characterService.getCharacter((Player) source);
        if (!pleader.isInPartyWith(character)) {
            return character;
        }
        throw args.createError(TextSerializers.FORMATTING_CODE.deserialize("&CNot a party member"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return characterService.getCharacter((Player) src)
                .getParty()
                .getPlayers()
                .stream()
                .map(ISpongeCharacter::getPlayer)
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<party_member>");
    }

}