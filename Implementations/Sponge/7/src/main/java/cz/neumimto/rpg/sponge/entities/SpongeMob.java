package cz.neumimto.rpg.sponge.entities;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public class SpongeMob extends AbstractMob<Living> implements ISpongeEntity<Living> {

    private Living entity;

    public SpongeMob(Living entity) {
        this.entity = entity;
    }

    //todo remove casts
    @Override
    public boolean isFriendlyTo(IActiveCharacter characterr) {
        Optional<Optional<UUID>> uuid = getEntity().get(Keys.TAMED_OWNER);
        if (uuid.isPresent()) {
            ISpongeCharacter character = (ISpongeCharacter) characterr;
            Optional<UUID> uuid1 = uuid.get();
            if (uuid1.isPresent()) {
                UUID uuid2 = uuid1.get();
                if (character.getPlayer().getUniqueId().equals(uuid2)) {
                    return true;
                }
                IParty<ISpongeCharacter> party = character.getParty();
                for (ISpongeCharacter iActiveCharacter : party.getPlayers()) {
                    UUID uniqueId = iActiveCharacter.getPlayer().getUniqueId();
                    if (uuid2.equals(uniqueId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void detach() {
        this.entityHealth = null;
        this.entity = null;
    }

    public Living getEntity() {
        return entity;
    }

    @Override
    public UUID getUUID() {
        return entity.getUniqueId();
    }

}
