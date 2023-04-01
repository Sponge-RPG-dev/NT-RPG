package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

public interface CharacterGainedExperiencesEvent {

    ActiveCharacter getCharacter();

    void setCharacter(ActiveCharacter character);

    double getExp();

    void setExp(double exp);

    String getExpSource();

    void setExpSource(String source);
}
