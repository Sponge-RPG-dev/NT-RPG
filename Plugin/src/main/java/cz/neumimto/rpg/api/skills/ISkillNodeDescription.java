package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.util.List;

public interface ISkillNodeDescription {

    List<String> getDescription(IActiveCharacter character);
}
