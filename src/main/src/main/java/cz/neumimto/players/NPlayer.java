package cz.neumimto.players;

import cz.neumimto.configuration.PluginConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by NeumimTo on 26.12.2014.
 */

public class NPlayer implements INPlayer {


    private final Vector<CharacterBase> characters = new Vector<>(PluginConfig.PLAYER_MAX_CHARS, 1);
    private IActiveCharacter activeIActiveCharacter;
    private Map<String, Long> cooldowns = new HashMap<>();


    public IActiveCharacter getActiveActiveCharacter() {
        return activeIActiveCharacter;
    }

    public void setActiveActiveCharacter(IActiveCharacter c) {
        this.activeIActiveCharacter = c;
    }

    public Vector<CharacterBase> getCharacterList() {
        return characters;
    }

}
