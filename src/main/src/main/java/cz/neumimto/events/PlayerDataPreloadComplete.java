package cz.neumimto.events;

import cz.neumimto.players.CharacterBase;

import java.util.List;
import java.util.UUID;

/**
 * Created by NeumimTo on 10.7.2015.
 */
public class PlayerDataPreloadComplete extends CancellableEvent {
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
}
