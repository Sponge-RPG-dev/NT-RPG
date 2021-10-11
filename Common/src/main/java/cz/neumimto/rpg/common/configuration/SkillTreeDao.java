package cz.neumimto.rpg.common.configuration;

import cz.neumimto.rpg.common.skills.tree.SkillTree;

import java.util.Map;

public interface SkillTreeDao {
    Map<String, SkillTree> getAll();

}
