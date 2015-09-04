package cz.neumimto.players;

import java.util.Vector;

/**
 * Created by NeumimTo on 10.2.2015.
 */
public interface INPlayer {
    Vector<CharacterBase> getCharacterList();

    IActiveCharacter getActiveActiveCharacter();

    void setActiveActiveCharacter(IActiveCharacter c);

}
