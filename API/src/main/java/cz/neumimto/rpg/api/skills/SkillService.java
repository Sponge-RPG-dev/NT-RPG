package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.api.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SkillService {

    void load();

    Map<String, ISkill> getSkills();

    SkillScriptHandlers getSkillHandler(String id);

    Map<String, SkillTree> getSkillTrees();

    SkillResult executeSkill(IActiveCharacter character, PlayerSkillContext esi);

    PlayerSkillContext invokeSkillByCombo(String combo, IActiveCharacter character);

    void registerAdditionalCatalog(ISkill extraCatalog);

    Optional<ISkill> getById(String id);

    Collection<ISkill> getAll();

    ISkill getSkillByLocalizedName(String name);

    void registerSkillAlternateName(String name, ISkill skill);

    ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader);

    void injectCatalogId(ISkill skill, String name);

    Optional<ISkillType> getSkillType(String id);

    void registerSkillType(ISkillType skillType);

    ISkillTreeInterfaceModel getGuiModelByCharacter(char c);

    Collection<String> getSkillNames();

    void registerSkillHandler(String key, SkillScriptHandlers toInterface);
}
