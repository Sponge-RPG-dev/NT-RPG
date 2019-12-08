package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.sponge.contexts.OnlinePlayer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.inventory.runewords.RuneWord;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.gui.SkillTreeViewModel;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandPermission("ntrpg.info.")
@CommandAlias("show|ninfo")
public class SpongeInfoCommands extends BaseCommand {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private VanillaMessaging messaging;

    @CommandAlias("skilltree")
    public void openSkillTreeMenuCommand(Player executor, @Optional ClassDefinition classDefinition) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        SkillTree skillTree = classDefinition.getSkillTree();
        if (skillTree == SkillTree.Default || skillTree == null) {
            character.sendMessage("Unknown class, or the class has no skilltree defined");
            return;
        }
        for (SkillTreeViewModel treeViewModel : character.getSkillTreeViewLocation().values()) {
            treeViewModel.setCurrent(false);
        }
        if (character.getSkillTreeViewLocation().get(skillTree.getId()) == null) {
            SkillTreeViewModel skillTreeViewModel = new SkillTreeViewModel();
            character.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
            skillTreeViewModel.setSkillTree(skillTree);
        } else {
            character.getSkillTreeViewLocation().get(skillTree.getId()).setCurrent(true);
        }
        Gui.openSkillTreeMenu(character);
    }

    @Subcommand("classes")
    @CommandPermission("%.classes")
    public void showClassesCommand(Player executor) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        characterService.resetAttributes(character);
        characterService.putInSaveQueue(character.getCharacterBase());
    }

    @Subcommand("character")
    @CommandPermission("%.player.characters.other")
    public void showOtherPlayerCharacterCommand(Player executor, OnlinePlayer target) {
        IActiveCharacter targett = characterService.getCharacter(executor);
        IActiveCharacter targett1 = characterService.getCharacter(target.player);
        Gui.showCharacterInfo(targett, targett1);
    }

    @Subcommand("character")
    @CommandPermission("%.player.characters.self")
    public void showPlayerCharacterCommand(Player executor) {
        IActiveCharacter targett = characterService.getCharacter(executor);
        Gui.showCharacterInfo(targett, targett);
    }

    //todo remove select button
    @Subcommand("characters")
    public void showPlayerCharactersCommand(Player executor) {
        IActiveCharacter target = characterService.getCharacter(executor);
        Gui.sendListOfCharacters(target, target.getCharacterBase());
    }

    @Subcommand("runeword")
    @CommandPermission("%.player.characters.other")
    public void showRunewordInfoCommand(Player executor, RuneWord runeword) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        messaging.displayRuneword(character, runeword, true);
    }

    @Subcommand("runeword allowed-items")
    public void displayRunewordAllowedItemsCommand(Player executor, RuneWord runeWord) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        messaging.displayRunewordAllowedItems(character, runeWord);
    }

    @Subcommand("runeword allowed-items")
    public void displayRunewordAllowedClassesCommand(Player executor, RuneWord runeWord) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        messaging.displayRunewordAllowedGroups(character, runeWord);
    }

    @Subcommand("runeword required-classes")
    public void displayRunewordRequiredClassesCommand(Player executor, RuneWord runeWord) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        messaging.displayRunewordRequiredGroups(character, runeWord);
    }

    @Subcommand("runeword blocked-classes")
    public void displayRunewordBlockedClassesCommand(Player executor, RuneWord runeWord) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        messaging.displayRunewordBlockedGroups(character, runeWord);
    }

    @Subcommand("attributes-initial")
    public void displayInitialClassAttributesCommand(Player executor, ClassDefinition classDefinition) {
        IActiveCharacter character = characterService.getCharacter(executor);
        Gui.displayInitialAttributes(classDefinition, character);
    }

    @Subcommand("properties-initial")
    public void displayInitialClassPropertiesCommand(Player executor, ClassDefinition classDefinition) {
        IActiveCharacter character = characterService.getCharacter(executor);
        Gui.displayInitialProperties(classDefinition, character);
    }

    @Subcommand("stats")
    public void displayCharacterStatsCommand(Player executor) {
        IActiveCharacter character = characterService.getCharacter(executor);
        Gui.sendStatus(character);
    }

}
