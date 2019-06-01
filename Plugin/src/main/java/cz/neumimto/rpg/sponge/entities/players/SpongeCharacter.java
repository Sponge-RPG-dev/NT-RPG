package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;

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
    public boolean isDetached() {
        return getPlayer() == null;
    }
}
