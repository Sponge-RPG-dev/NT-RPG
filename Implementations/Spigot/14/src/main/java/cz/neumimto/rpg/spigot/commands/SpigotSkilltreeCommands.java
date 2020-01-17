package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpigotSkillTreeViewModel;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
@CommandAlias("skilltree")
public class SpigotSkilltreeCommands extends BaseCommand {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Inject
    private SkillService skillService;

    @Subcommand("view")
    public void openSkillTreeCommand(Player executor, ClassDefinition classDefinition) {
        ISpigotCharacter character = characterService.getCharacter(executor);

        skillsCommandFacade.openSkillTreeCommand(character, classDefinition);
    }

    @Subcommand("north")
    public void skillTreeOptionNorth(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        SpigotSkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().value -= 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("south")
    public void skillTreeOptionSouth(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        SpigotSkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().value += 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("west")
    public void skillTreeOptionWest(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        SpigotSkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().key += 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("east")
    public void skillTreeOptionEast(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        SpigotSkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.getLocation().key -= 1;
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("mode")
    public void skillTreeOptionMode(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        SpigotSkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
        viewModel.setInteractiveMode(viewModel.getInteractiveMode().opposite());
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));
    }

    @Subcommand("skill")
    public void skillTreeOptionSkill(Player executor, ISkill skill) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        SpigotSkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();

        viewModel.setInteractiveMode(viewModel.getInteractiveMode().opposite());
        Rpg.get().scheduleSyncLater(() -> Gui.moveSkillTreeMenu(character));

        if (viewModel.getInteractiveMode() == SpigotSkillTreeViewModel.InteractiveMode.FAST) {

            ClassDefinition classDefinition = viewModel.getViewedClass();
            PlayerClassData playerClassData = character.getClasses().get(classDefinition.getName());

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
