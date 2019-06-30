package cz.neumimto.rpg.api.configuration;

import cz.neumimto.rpg.api.skills.tree.SkillTree;

import java.util.Map;

public interface SkillTreeDao {
    Map<String, SkillTree> getAll();

}
