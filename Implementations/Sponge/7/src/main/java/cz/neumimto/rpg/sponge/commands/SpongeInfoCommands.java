package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.sponge.contexts.OnlinePlayer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.common.inventory.runewords.RuneWord;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("show|ninfo")
public class SpongeInfoCommands extends BaseCommand {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private VanillaMessaging messaging;

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @CommandAlias("skilltree")
    public void openSkillTreeMenuCommand(Player executor, @Optional ClassDefinition classDefinition) {
        IActiveCharacter character = characterService.getCharacter(executor);
        characterCommandFacade.openSKillTreeMenu(character, classDefinition);
    }

    @Subcommand("classes")
    @CommandPermission("ntrpg.info.classes")
    public void showClassesCommand(Player executor, @Optional String type) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        if (type == null) {
            Gui.sendClassTypes(character);
        } else {
            Gui.sendClassesByType(character, type);
        }

    }

    @Subcommand("character")
    @CommandPermission("ntrpg.info.player.characters.other")
    public void showOtherPlayerCharacterCommand(Player executor, OnlinePlayer target) {
        IActiveCharacter targett = characterService.getCharacter(executor);
        IActiveCharacter targett1 = characterService.getCharacter(target.player);
        Gui.showCharacterInfo(targett, targett1);
    }

    @Subcommand("character")
    @CommandPermission("ntrpg.info.player.characters.self")
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
    @CommandPermission("ntrpg.info.player.characters.other")
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

    @Subcommand("class")
    @CommandPermission("ntrpg.info.class")
    public void showClassCommand(Player executor, ClassDefinition classDefinition, @Optional String back) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        messaging.showClassInfo(character, classDefinition);
    }

    @Subcommand("class-weapons")
    @CommandPermission("ntrpg.info.class")
    public void showClassWeapons(Player player, ClassDefinition cc) {
        IActiveCharacter target = characterService.getCharacter(player);
        Gui.displayClassWeapons(cc, target);
    }

    @Subcommand("class-armor")
    @CommandPermission("ntrpg.info.class")
    public void showClassArmor(Player player, ClassDefinition cc) {
        IActiveCharacter target = characterService.getCharacter(player);
        Gui.displayClassArmor(cc, target);
    }
}
