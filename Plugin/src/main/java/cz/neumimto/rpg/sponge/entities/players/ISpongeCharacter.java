package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.party.SpongeParty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public interface ISpongeCharacter extends IActiveCharacter<Player, SpongeParty>, ISpongeEntity<Player> {

    @Override
    default void sendMessage(String message) {
        getPlayer().sendMessage(TextHelper.parse(message));
    }

    default Player getPlayer() {
        return Sponge.getServer().getPlayer(getUUID()).orElse(null);
    }

    default Player getEntity() {
        return getPlayer();
    }


}
