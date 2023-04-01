package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import java.util.List;

public interface ISkillNodeDescription {

    List<String> getDescription(ActiveCharacter character);
}
