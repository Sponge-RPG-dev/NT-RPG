package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;

@Singleton
@CommandAlias("char|c")
public class SpigotCharacterCommands extends BaseCommand {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SpigotRpgPlugin plugin;

    @Inject
    private ClassService classService;

    @Inject
    private SpigotGui spigotGui;

    @Default
    public void menu(Player executor) {
        IActiveCharacter character = characterService.getCharacter(executor);
        Gui.displayCharacterMenu(character);
    }

    @Subcommand("list")
    public void characterList(Player executor) {
        IActiveCharacter character = characterService.getCharacter(executor);
        Gui.sendListOfCharacters(character, character.getCharacterBase());
    }

    @Subcommand("create")
    public void createCharacter(Player executor, String name) {
        UUID uuid = executor.getUniqueId();

        characterCommandFacade.commandCreateCharacter(uuid, name, executor.getName(), actionResult -> {
            executor.sendMessage(actionResult.getMessage());
        });
    }

    @Subcommand("switch")
    public void switchCharacter(Player executor, String name) {
        IActiveCharacter character = characterService.getCharacter(executor);

        characterCommandFacade.commandSwitchCharacter(character, name, runnable -> {
            Rpg.get().scheduleSyncLater(runnable);
        });
    }

    @Subcommand("choose class")
    public void chooseCharacterClass(Player executor, ClassDefinition classDefinition) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        characterService.addNewClass(character, classDefinition);
    }

    @Subcommand("attribute add")
    public void attributesAdd(Player executor, AttributeConfig a, @Default("false") @Optional boolean ui, @Optional int slotMod) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        Map<String, Integer> attributesTransaction = character.getAttributesTransaction();
        Integer integer = attributesTransaction.get(a.getId());
        attributesTransaction.put(a.getId(), integer + 1);
        if (ui) {
            spigotGui.refreshAttributeView(executor, character, slotMod, a);
        }
    }

    @Subcommand("attributes")
    public void attributes(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        spigotGui.displayCharacterAttributes(executor, character);
    }

    @Subcommand("weapons")
    public void weapons(Player executor, @Default("0") int page) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        spigotGui.displayCharacterWeapons(character, page);
    }

    @Subcommand("armor")
    public void armor(Player executor, @Default("0") int page) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        spigotGui.displayCharacterArmor(character, page);
    }

    @Subcommand("attributes tx-commit")
    public void attributesCommit(Player executor) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        characterCommandFacade.commandCommitAttribute(character);
    }



}
