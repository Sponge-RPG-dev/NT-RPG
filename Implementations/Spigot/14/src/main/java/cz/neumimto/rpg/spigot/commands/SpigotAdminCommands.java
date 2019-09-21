package cz.neumimto.rpg.spigot.commands;


import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Singleton;
import java.lang.annotation.Inherited;

@Singleton
@CommandAlias("nadmin|na")
public class SpigotAdminCommands extends AbstractAdminCommands<CommandSender, Player> {

    @Override
    protected IActiveCharacter toCharacter(Player player) {
        return characterService.getCharacter(player.getUniqueId());
    }

    @Override
    @Subcommand("effect add")
    @Description("Adds effect, managed by rpg plugin, to the player")
    public void effectAddCommand(CommandSender commandSender, @Flags("taget") Player target, IGlobalEffect effect, long duration, String[] args) {
        super.effectAddCommand(commandSender, target, effect, duration, args);
    }

    @Override
    protected void sendMessageC(CommandSender commandSender, String message) {
        commandSender.sendMessage(message);
    }

    @Override
    protected void sendMessageT(Player player, String message) {
        player.sendMessage(message);
    }
}
