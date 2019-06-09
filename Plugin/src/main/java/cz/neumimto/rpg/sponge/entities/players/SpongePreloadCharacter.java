package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.common.entity.players.PlayerNotInGameException;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.sponge.entities.players.party.SpongeParty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public class SpongePreloadCharacter extends PreloadCharacter<Player, SpongeParty> implements ISpongeCharacter {

    public SpongePreloadCharacter(UUID uuid) {
        super(uuid);
    }

    @Override
    public Player getPlayer() {
        Optional<Player> player = Sponge.getServer().getPlayer(getUUID());
        if (player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotInGameException(String.format(
                    "Player object with uuid=%s has not been constructed yet. Calling PreloadCharacter.getCharacter in a wrong state", getUUID()), this);
        }
    }

    @Override
    public Player getEntity() {
        return getPlayer();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void sendMessage(int channel, String message) {
        getPlayer().sendMessage(TextHelper.parse(message));
    }

}
