package cz.neumimto.rpg.common.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.utils.ActionResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("char|c")
@CommandPermission("ntrpg.player.char")
public class CharacterCommands extends BaseCommand {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Inject
    private CharacterService characterService;

    @Inject
    private ClassService classService;

    @Default
    public void menu(ActiveCharacter character) {
        Gui.displayCharacterMenu(character);
        character.getGuiCommandHistory().add("Ntrpg:char");
    }

    @Subcommand("list")
    public void characterList(ActiveCharacter character) {
        Gui.sendListOfCharacters(character, character.getCharacterBase());
    }


    @Subcommand("choose class")
    public void chooseCharacterClass(ActiveCharacter character, ClassDefinition classDefinition) {
        ActionResult actionResult = characterService.canGainClass(character, classDefinition);
        if (actionResult.isOk()) {
            characterService.addNewClass(character, classDefinition);
        } else {
            String message = actionResult.getMessage();
            character.sendMessage(message);
        }
    }

    @Subcommand("attributes")
    public void attributes(ActiveCharacter character) {
        Gui.displayCharacterAttributes(character);
        character.getGuiCommandHistory().add("Ntrpg:attributes");
    }

    @Subcommand("weapons")
    public void weapons(ActiveCharacter character, @Default("0") int page) {
        Gui.displayCharacterWeapons(character, page);
        character.getGuiCommandHistory().add("Ntrpg:char weapons " + page);
    }

    @Subcommand("armor")
    public void armor(ActiveCharacter character, @Default("0") int page) {
        Gui.displayCharacterArmor(character, page);
        character.getGuiCommandHistory().add("Ntrpg:char armor " + page);
    }

    @Subcommand("healthscale")
    public void attributesCommit(ActiveCharacter character, double scale) {
        characterService.setHeathscale(character, scale);
    }

    @Subcommand("attributes-commit")
    public void attributesCommitCommand(ActiveCharacter character) {
        characterCommandFacade.commandCommitAttribute(character);
    }

    @Subcommand("attribute add")
    public void attributesCommitCommand(ActiveCharacter character, AttributeConfig attribute, @Default("1") int amount) {
        character.getAttributesTransaction().put(attribute.getId(), amount);
    }

    @Subcommand("attribute respec")
    public void attributeRespecCommand(ActiveCharacter character) {
        characterService.resetAttributes(character);
        characterService.putInSaveQueue(character.getCharacterBase());
    }

    @Subcommand("spellbook")
    public void spellbookCommand(ActiveCharacter character) {
        Gui.displaySpellbook(character);
        character.getGuiCommandHistory().add("Ntrpg:char spellbook");
    }


    @Subcommand("spellbook-commit")
    public void spellbookCommit(ActiveCharacter character) {
        characterService.updateSpellbook(character);
    }


}

