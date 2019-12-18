package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
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
}
