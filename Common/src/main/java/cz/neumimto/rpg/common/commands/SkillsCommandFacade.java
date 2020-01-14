package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.gui.SkillTreeViewModel;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.mods.ResultNotificationSkillExecutor;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;

@Singleton
public class SkillsCommandFacade {

    @Inject
    private LocalizationService localizationService;

    @Inject
    private SkillService skillService;

    public void processSkillAction(IActiveCharacter character, ISkill skill, SkillAction action, String flagData) {
        if (action == null) {
            executeSkill(character, skill);
            return;
        }
        PlayerClassData classByName = character.getClassByName(flagData);
        if (classByName != null) {
            ClassDefinition skillTree = classByName.getClassDefinition();
            switch (action) {
                case LEARN:
                    learnSkill(character, skill, skillTree);
                    return;
                case REFUND:
                    refundSkill(character, skill, skillTree);
                    return;
                case UPGRADE:
                    upgradeSkill(character, skill, skillTree);
                    return;
            }
        }
    }

    public void executeSkill(IActiveCharacter character, ISkill skill) {
        PlayerSkillContext info = character.getSkillInfo(skill.getId());
        if (info == PlayerSkillContext.EMPTY || info == null) {
            character.sendMessage(localizationService.translate(LocalizationKeys.CHARACTER_DOES_NOT_HAVE_SKILL, Arg.arg("skill", skill.getName())));
            return;
        }
        skillService.executeSkill(character, info, ResultNotificationSkillExecutor.INSTANCE);
    }

    public void learnSkill(IActiveCharacter character, ISkill skill, ClassDefinition aClass) {
        if (aClass.getSkillTree() != null) {
            Map<String, PlayerClassData> classes = character.getClasses();
            PlayerClassData playerClassData = classes.get(aClass.getName());
            aClass.getSkillTreeType().processLearnSkill(character, playerClassData, skill);
        } else {
            String msg = localizationService.translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
            character.sendMessage(msg);
        }
    }

    public void refundSkill(IActiveCharacter character, ISkill skill, ClassDefinition aClass) {
        if (aClass.getSkillTree() != null) {
            Map<String, PlayerClassData> classes = character.getClasses();
            PlayerClassData playerClassData = classes.get(aClass.getName());
            aClass.getSkillTreeType().processLearnSkill(character, playerClassData, skill);
        } else {
            String msg = localizationService.translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
            character.sendMessage(msg);
        }
    }

    public void upgradeSkill(IActiveCharacter character, ISkill skill, ClassDefinition aClass) {
        if (aClass.getSkillTree() != null) {
            Map<String, PlayerClassData> classes = character.getClasses();
            PlayerClassData playerClassData = classes.get(aClass.getName());
            aClass.getSkillTreeType().processUpgradeSkill(character, playerClassData, skill);
        } else {
            String msg = localizationService.translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
            character.sendMessage(msg);
        }
    }

    public void openSkillTreeCommand(IActiveCharacter character, ClassDefinition classDefinition) {
        if (classDefinition == null) {
            PlayerClassData primaryClass = character.getPrimaryClass();
            if (primaryClass == null) {
                String translate = localizationService.translate(LocalizationKeys.NO_PRIMARY_CLASS);
                character.sendMessage(translate);
                return;
            }
            classDefinition = primaryClass.getClassDefinition();
        }
        if (classDefinition != null) {
            SkillTree skillTree = classDefinition.getSkillTree();
            Collection<SkillTreeViewModel> values = character.getSkillTreeViewLocation().values();
            for (SkillTreeViewModel treeViewModel : values) {
                treeViewModel.setCurrent(false);
            }
            SkillTreeViewModel skillTreeViewModel = (SkillTreeViewModel) character.getSkillTreeViewLocation().get(skillTree.getId());
            if (skillTreeViewModel == null) {
                skillTreeViewModel = SkillTreeViewModel.get();
                character.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
                skillTreeViewModel.setSkillTree(skillTree);
            } else {
                skillTreeViewModel.setCurrent(true);
                skillTreeViewModel.reset();
            }
            skillTreeViewModel.setViewedClass(classDefinition);
            Gui.openSkillTreeMenu(character);
        }
    }

    public static enum SkillAction {
        LEARN,
        UPGRADE,
        REFUND
    }
}
