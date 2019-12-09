package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.gui.SpongeSkillTreeViewModel;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("skilltree")
public class SpongeSkilltreeCommands extends BaseCommand {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private SkillsCommandFacade skillsCommandFacade;

    @Default
    public void openSkillTreeCommand(Player executor, ClassDefinition classDefinition) {
        ISpongeCharacter character = characterService.getCharacter(executor);

        openSkillTreeCommand(character, classDefinition);
    }

    public void openSkillTreeCommand(ISpongeCharacter character, ClassDefinition classDefinition) {
        if (classDefinition != null) {
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
            for (SpongeSkillTreeViewModel treeViewModel : character.getSkillTreeViewLocation().values()) {
                treeViewModel.setCurrent(false);
            }
            SpongeSkillTreeViewModel skillTreeViewModel = character.getSkillTreeViewLocation().get(skillTree.getId());
            if (skillTreeViewModel == null) {
                skillTreeViewModel = new SpongeSkillTreeViewModel();
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
}
