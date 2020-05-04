package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import java.util.List;

public class SkillNodeDescription implements ISkillNodeDescription {

    private final List<String> description;

    public SkillNodeDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public List<String> getDescription(IActiveCharacter character) {
        return description;
    }
}
