package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

import java.util.List;

public interface ISkillNodeDescription {

    List<String> getDescription(IActiveCharacter character);
}
