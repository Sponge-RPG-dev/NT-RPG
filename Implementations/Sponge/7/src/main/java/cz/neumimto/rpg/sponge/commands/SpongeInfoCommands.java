package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
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

    @Subcommand("classes")
    public void showClassesCommand(Player executor) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        characterService.resetAttributes(character);
        characterService.putInSaveQueue(character.getCharacterBase());
    }

    @CommandPermission("list.character.others")
    @Subcommand("player")
    public void showPlayerClasses(Player executor, @Optional @Flags("target") Player target) {
        printPlayerInfo(executor, target);
    }

    @Subcommand("character")
    public void showPlayerCharacterCommand(Player executor) {
        IActiveCharacter target = characterService.getCharacter(executor);
        Gui.showCharacterInfo(target, target);
    }

    @Subcommand("characters")
    public void showPlayerCharactersCommand(Player executor) {
        IActiveCharacter target = characterService.getCharacter(executor);
        Gui.sendListOfCharacters(target, target.getCharacterBase());
    }


    @Subcommand("runeword")
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
