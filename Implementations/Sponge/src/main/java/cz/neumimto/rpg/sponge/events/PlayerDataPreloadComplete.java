

package cz.neumimto.rpg.sponge.events;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.sponge.NEventContextKeys;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.List;
import java.util.UUID;

/**
 * Created by NeumimTo on 10.7.2015.
 */
public class PlayerDataPreloadComplete extends AbstractEvent {

    private UUID player;
    private List<CharacterBase> characterBases;

    public PlayerDataPreloadComplete(UUID player, List<CharacterBase> characterBases) {
        this.player = player;
        this.characterBases = characterBases;
    }

    public UUID getPlayer() {
        return player;
    }

    public List<CharacterBase> getCharacterBases() {
        return characterBases;
    }

    public void setCharacterBases(List<CharacterBase> characterBases) {
        this.characterBases = characterBases;
    }

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.builder().add(NEventContextKeys.GAME_PROFILE, player).build(), player);
    }
}
