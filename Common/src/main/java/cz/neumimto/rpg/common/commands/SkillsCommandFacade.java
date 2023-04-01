package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.common.skills.tree.SkillTree;

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

    //todo this entire process may be done async
    public void processSkillAction(ActiveCharacter character, ISkill skill, SkillAction action, String flagData) {
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

    public void executeSkill(ActiveCharacter character, String skillId) {
        PlayerSkillContext info = character.getSkillInfo(skillId);
        if (info == PlayerSkillContext.EMPTY || info == null) {
            Arg arg = Arg.arg("skill", skillId);
            character.sendMessage(
                    localizationService.translate(LocalizationKeys.CHARACTER_DOES_NOT_HAVE_SKILL,
                            arg));
            return;
        }
        skillService.executeSkill(character, info);
    }

    public void executeSkill(ActiveCharacter character, ISkill skill) {
        executeSkill(character, skill.getId());
    }

    public void learnSkill(ActiveCharacter character, ISkill skill, ClassDefinition aClass) {
        if (aClass.getSkillTree() != null) {
            Map<String, PlayerClassData> classes = character.getClasses();
            PlayerClassData playerClassData = classes.get(aClass.getName().toLowerCase());
            aClass.getSkillTreeType().processLearnSkill(character, playerClassData, skill);
        } else {
            String msg = localizationService.translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
            character.sendMessage(msg);
        }
    }

    public void refundSkill(ActiveCharacter character, ISkill skill, ClassDefinition aClass) {
        if (aClass.getSkillTree() != null) {
            Map<String, PlayerClassData> classes = character.getClasses();
            PlayerClassData playerClassData = classes.get(aClass.getName());
            aClass.getSkillTreeType().processLearnSkill(character, playerClassData, skill);
        } else {
            String msg = localizationService.translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
            character.sendMessage(msg);
        }
    }

    public void upgradeSkill(ActiveCharacter character, ISkill skill, ClassDefinition aClass) {
        if (aClass.getSkillTree() != null) {
            Map<String, PlayerClassData> classes = character.getClasses();
            PlayerClassData playerClassData = classes.get(aClass.getName().toLowerCase());
            aClass.getSkillTreeType().processUpgradeSkill(character, playerClassData, skill);
        } else {
            String msg = localizationService.translate(LocalizationKeys.CLASS_HAS_NO_SKILLTREE, Arg.arg("class", aClass.getName()));
            character.sendMessage(msg);
        }
    }

    public void openSkillTreeCommand(ActiveCharacter character, ClassDefinition classDefinition) {
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

    public enum SkillAction {
        LEARN,
        UPGRADE,
        REFUND
    }
}
