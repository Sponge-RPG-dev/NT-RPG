package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.gui.SkillTreeViewModel;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

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

    @Subcommand("view")
    @CommandCompletion("@issuerclasses")
    public void openSkillTreeCommand(IActiveCharacter character, ClassDefinition classDefinition) {
        skillsCommandFacade.openSkillTreeCommand(character, classDefinition);
    }

    @Subcommand("north")
    public void skillTreeOptionNorth(IActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().key -= 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("south")
    public void skillTreeOptionSouth(IActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().key += 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("west")
    public void skillTreeOptionWest(IActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().value += 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("east")
    public void skillTreeOptionEast(IActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().value -= 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("mode")
    public void skillTreeOptionMode(IActiveCharacter character) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.setInteractiveMode(viewModel.getInteractiveMode().opposite());
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("skill")
    public void skillTreeOptionSkill(IActiveCharacter character, ISkill skill) {
        SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();

        viewModel.setInteractiveMode(viewModel.getInteractiveMode().opposite());
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));

        if (viewModel.getInteractiveMode() == SkillTreeViewModel.InteractiveMode.FAST) {

            ClassDefinition classDefinition = viewModel.getViewedClass();
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
