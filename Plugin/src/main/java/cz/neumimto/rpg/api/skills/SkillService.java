package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SkillService {

    void load();

    void init();

    Map<String, ISkill> getSkills();

    Map<String, SkillTree> getSkillTrees();

    void executeSkill(IActiveCharacter character, ISkill skill, SkillExecutorCallback callback);

    void executeSkill(IActiveCharacter character, PlayerSkillContext esi, SkillExecutorCallback callback);

    PlayerSkillContext invokeSkillByCombo(String combo, IActiveCharacter character);

    void reloadSkillTrees();

    void registerAdditionalCatalog(ISkill extraCatalog);

    Optional<ISkill> getById(String id);

    Collection<ISkill> getAll();

    ISkill getSkillByLocalizedName(String name);

    void registerSkillAlternateName(String name, ISkill skill);

    ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader);

    void injectCatalogId(ISkill skill, String name);

    Optional<ISkillType> getSkillType(String id);

    void registerSkillType(ISkillType skillType);
}
