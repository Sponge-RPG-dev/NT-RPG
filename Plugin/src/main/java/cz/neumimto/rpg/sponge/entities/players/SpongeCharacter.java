package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.UUID;

public class SpongeCharacter extends ActiveCharacter implements ISpongeCharacter {

    public SpongeCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        super(uuid, base, propertyCount);
    }

    @Override
    public UUID getUUID() {
        return pl;
    }

    @Override
    public void sendMessage(int channel, String message) {

    }

    @Override
    public void sendMessage(MessageChannel channel, String message) {
        Player player = getEntity();
        switch (channel) {

        }
    }

    @Override
    public boolean isDetached() {
        return getPlayer() == null;
    }

    @Override
    public void sendMessage(String message) {

    }
}
