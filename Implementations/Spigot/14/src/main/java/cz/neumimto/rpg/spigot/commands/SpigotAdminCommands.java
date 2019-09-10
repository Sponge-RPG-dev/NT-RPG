package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.commands.CommandProcessingException;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("nadmin|na")
public class SpigotAdminCommands extends BaseCommand {

    @Inject
    private AdminCommandFacade adminCommandFacade;

    @Inject
    private SpigotCharacterService characterService;

    @Subcommand("effect-add")
    @Description("Adds effect, managed by rpg plugin, to the player")
    public void effectAddCommand(Player executor, Player target, IGlobalEffect effect, long duration, @Default("{}") String[] args) {
        String data = String.join("", args);

        IActiveCharacter character = characterService.getCharacter(target);

        try {
            adminCommandFacade.commandAddEffectToPlayer(data, effect, duration, character);
        } catch (CommandProcessingException e) {
            executor.sendMessage(e.getMessage());
        }
    }
}
