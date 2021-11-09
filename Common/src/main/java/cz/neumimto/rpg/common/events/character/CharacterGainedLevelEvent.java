package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;

/**
 * Created by NeumimTo on 27.12.2015.
 */
public interface CharacterGainedLevelEvent extends TargetCharacterEvent {

    PlayerClassData getPlayerClassData();

    void setPlayerClassData(PlayerClassData data);

    int getLevel();

    void setLevel(int level);

    int getSkillpointsPerLevel();

    void setSkillpointsPerLevel(int skillpointsPerLevel);

    int getAttributepointsPerLevel();

    void setAttributepointsPerLevel(int attributepointsPerLevel);

}
