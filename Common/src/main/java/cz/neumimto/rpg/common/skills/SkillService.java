package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.common.skills.tree.SkillTree;

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

    default ISkill getSkillById(String id) {
        return getSkills().get(id);
    }
}
