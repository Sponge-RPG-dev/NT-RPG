package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;

import java.util.Map;

public interface IPlayerSkillHandler {

    default PlayerSkillContext get(ISkill iSkill) {
        return get(iSkill.getId());
    }

    void add(String iSkill, PlayerSkillContext source);

    PlayerSkillContext getByName(String name);

    PlayerSkillContext get(String id);

    default void add(ISkill skill, PlayerSkillContext origin) {
        add(skill.getId(), origin);
    }

    PlayerSkillContext get(String id, ClassDefinition classDefinition);

    void remove(ISkill skill, ClassDefinition origin);

    Map<String, PlayerSkillContext> getSkills();

    void clear();

    boolean contains(String name);

    Map<String, PlayerSkillContext> getSkillsByName();
}
