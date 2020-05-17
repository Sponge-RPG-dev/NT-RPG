package cz.neumimto.rpg.common.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("char|c")
public class CharacterCommands extends BaseCommand {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Inject
    private CharacterService characterService;

    @Inject
    private ClassService classService;

    @Default
    public void menu(IActiveCharacter character) {
        Gui.displayCharacterMenu(character);
    }

    @Subcommand("list")
    public void characterList(IActiveCharacter character) {
        Gui.sendListOfCharacters(character, character.getCharacterBase());
    }


    @Subcommand("choose class")
    public void chooseCharacterClass(IActiveCharacter character, ClassDefinition classDefinition) {
        characterService.addNewClass(character, classDefinition);
    }

    @Subcommand("attributes")
    public void attributes(IActiveCharacter character) {
        Gui.displayCharacterAttributes(character);
    }

    @Subcommand("weapons")
    public void weapons(IActiveCharacter character, @Default("0") int page) {
        Gui.displayCharacterWeapons(character, page);
    }

    @Subcommand("armor")
    public void armor(IActiveCharacter character, @Default("0") int page) {
        Gui.displayCharacterArmor(character, page);
    }

    @Subcommand("healthscale")
    public void attributesCommit(IActiveCharacter character, double scale) {
        characterService.setHeathscale(character, scale);
    }

    @Subcommand("attributes-commit")
    public void attributesCommitCommand(IActiveCharacter character) {
        characterCommandFacade.commandCommitAttribute(character);
    }

    @Subcommand("attribute add")
    public void attributesCommitCommand(IActiveCharacter character, AttributeConfig attribute, @Default("1") int amount) {
        character.getAttributesTransaction().put(attribute.getId(), amount);
    }

    @Subcommand("attribute respec")
    public void attributeRespecCommand(IActiveCharacter character) {
        characterService.resetAttributes(character);
        characterService.putInSaveQueue(character.getCharacterBase());
    }

    @Subcommand("spellbook")
    public void spellbookCommand(IActiveCharacter character) {
        Gui.displaySpellbook(character);
    }
}

