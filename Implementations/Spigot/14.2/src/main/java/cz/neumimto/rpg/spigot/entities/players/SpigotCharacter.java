package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpigotCharacter extends ActiveCharacter<Player, SpigotParty> implements ISpigotCharacter {

    public SpigotCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        super(uuid, base, propertyCount);
    }

    @Override
    public void sendMessage(String message) {
        getPlayer().spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(int channel, String message) {

    }

    @Override
    public void sendNotification(String message) {
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    @Override
    public Player getEntity() {
        return getPlayer();
    }

    @Override
    public boolean isDetached() {
        return getPlayer() == null;
    }

    private Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

}
