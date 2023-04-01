package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.common.skills.tree.SkillTree;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
@CommandAlias("skilltree")
public class SkilltreeCommands extends BaseCommand {

    @Inject
    private CharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Inject
    private SkillService skillService;

    @Default
    @CommandCompletion("@issuerclasses")
    public void _openSkillTreeCommand(ActiveCharacter character, ClassDefinition classDefinition) {
        openSkillTreeCommand(character, classDefinition);
    }

    @Subcommand("view")
    @CommandCompletion("@issuerclasses")
    public void openSkillTreeCommand(ActiveCharacter character, ClassDefinition classDefinition) {
        skillsCommandFacade.openSkillTreeCommand(character, classDefinition);
    }

    @Private
    @Subcommand("north")
    public void skillTreeOptionNorth(ActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().key -= 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Private
    @Subcommand("south")
    public void skillTreeOptionSouth(ActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().key += 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Private
    @Subcommand("west")
    public void skillTreeOptionWest(ActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().value += 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Private
    @Subcommand("east")
    public void skillTreeOptionEast(ActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().value -= 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Private
    @Subcommand("mode")
    public void skillTreeOptionMode(ActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.setInteractiveMode(viewModel.getInteractiveMode().opposite());
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Private
    @Subcommand("skill")
    public void skillTreeOptionSkill(ActiveCharacter character, ISkill skill) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();

        if (viewModel.getInteractiveMode() == SkillTreeViewModel.InteractiveMode.FAST) {

            ClassDefinition classDefinition = viewModel.getViewedClass();
            if (character.getClassByName(classDefinition.getName()) == null) {
                character.sendMessage("You dont have class " + classDefinition.getName());
                return;
            }
            PlayerClassData playerClassData = character.getClassByName(classDefinition.getName());

            if (character.getSkill(skill.getId()) == null) {
                classDefinition.getSkillTreeType().processLearnSkill(character, playerClassData, skill);
            } else {
                classDefinition.getSkillTreeType().processUpgradeSkill(character, playerClassData, skill);
            }

            //redraw
            Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));

        } else {
            SkillTree tree = viewModel.getSkillTree();

            Rpg.get().scheduleSyncLater(() -> Gui.displaySkillDetailsInventoryMenu(character, tree, skill.getId()));
        }
    }

}
