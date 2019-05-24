package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.common.reloading.Reload;
import cz.neumimto.rpg.common.reloading.ReloadService;
import cz.neumimto.rpg.sponge.gui.SkillTreeInterfaceModel;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ISkillService {
    void load();

    @Reload(on = ReloadService.PLUGIN_CONFIG)
    void initGuis();

    Map<String, ISkill> getSkills();

    Map<String, SkillTree> getSkillTrees();

    void executeSkill(IActiveCharacter character, ISkill skill, SkillExecutorCallback callback);

    void executeSkill(IActiveCharacter character, PlayerSkillContext esi, SkillExecutorCallback callback);

    PlayerSkillContext invokeSkillByCombo(String combo, IActiveCharacter character);

    void reloadSkillTrees();

    SkillTreeInterfaceModel getGuiModelByCharacter(Character character);

    SkillTreeInterfaceModel getGuiModelById(Short k);

    void registerAdditionalCatalog(ISkill extraCatalog);

    Optional<ISkill> getById(String id);

    Collection<ISkill> getAll();

    ISkill getSkillByLocalizedName(String name);

    void registerSkillAlternateName(String name, ISkill skill);

    ISkill skillDefinitionToSkill(ScriptSkillModel scriptSkillModel, ClassLoader classLoader);

    void injectCatalogId(ISkill skill, String name);
}
