package cz.neumimto.rpg.spigot.entities.players;

import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.entity.players.PlayerNotInGameException;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotParty;
import cz.neumimto.rpg.spigot.gui.SpigotSkillTreeViewModel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpigotPreloadCharacter extends PreloadCharacter<Player, SpigotParty> implements ISpigotCharacter {

    public SpigotPreloadCharacter(UUID uuid) {
        super(uuid);
    }

    @Override
    public Player getPlayer() {
        Player player = Bukkit.getServer().getPlayer(getUUID());
        if (player != null) {
            return player;
        } else {
            throw new PlayerNotInGameException(String.format(
                    "Player object with uuid=%s has not been constructed yet. Calling PreloadCharacter.getCharacter in a wrong state", getUUID()), this);
        }
    }

    @Override
    public Player getEntity() {
        return getPlayer();
    }

    @Override
    public Map<String, SpigotSkillTreeViewModel> getSkillTreeViewLocation() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Integer> getAttributesTransaction() {
        return Collections.emptyMap();
    }

    @Override
    public void setAttributesTransaction(HashMap<String, Integer> map) {

    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public ISkill skillOrEffectDamageCayse() {
        return null;
    }

    @Override
    public ISpigotEntity setSkillOrEffectDamageCause(ISkill rpgElement) {
        return this;
    }

    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    @Override
    public void sendNotification(String message) {
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public String getPlayerAccountName() {
        return getPlayer().getName();
    }
}
