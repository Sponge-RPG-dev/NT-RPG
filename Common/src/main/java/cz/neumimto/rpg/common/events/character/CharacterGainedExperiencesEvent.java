package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

public interface CharacterGainedExperiencesEvent {

    IActiveCharacter getCharacter();

    void setCharacter(IActiveCharacter character);

    double getExp();

    void setExp(double exp);

    String getExpSource();

    void setExpSource(String source);
}
