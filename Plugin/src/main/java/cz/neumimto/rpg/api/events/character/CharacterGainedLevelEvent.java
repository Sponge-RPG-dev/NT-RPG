package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.players.PlayerClassData;

/**
 * Created by NeumimTo on 27.12.2015.
 */
public interface CharacterGainedLevelEvent extends TargetCharacterEvent {

    PlayerClassData getPlayerClassData();

    void setPlayerClassData(PlayerClassData data);

    int getLevel();

    void setLevel(int level);

    int getSkillpointsPerLevel();

    int setSkillpointsPerLevel();

    void setSkillpointsPerLevel(int skillpointsPerLevel);

    int getAttributepointsPerLevel();

    void setAttributepointsPerLevel(int attributepointsPerLevel);

}
