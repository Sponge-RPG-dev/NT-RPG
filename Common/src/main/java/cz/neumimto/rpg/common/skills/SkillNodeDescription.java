package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import java.util.List;

public class SkillNodeDescription implements ISkillNodeDescription {

    private final List<String> description;

    public SkillNodeDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public List<String> getDescription(ActiveCharacter character) {
        return description;
    }
}
