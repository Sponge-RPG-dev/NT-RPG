package cz.neumimto.rpg.spigot.listeners;

import com.google.auto.service.AutoService;
import com.google.inject.Singleton;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.entity.UserActionType;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.services.IRpgListener;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Inject;

@Singleton
@AutoService(IRpgListener.class)
@ResourceLoader.ListenerClass
public class SpigotComboListener implements IRpgListener {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SpigotRpg spigotRpg;

    @EventHandler
    public void onRMBClick(PlayerInteractEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (spigotRpg.isDisabledInWorld(e.getPlayer())) {
            return;
        }
        ActiveCharacter character = characterService.getCharacter(e.getPlayer());
        e.setCancelled(characterService.processUserAction(character, UserActionType.R));

    }

    @EventHandler
    public void onLMBClick(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (spigotRpg.isDisabledInWorld(e.getPlayer())) {
            return;
        }
        ActiveCharacter character = characterService.getCharacter(e.getPlayer());
        e.setCancelled(characterService.processUserAction(character, UserActionType.L));

    }

    @EventHandler
    public void onQPress(PlayerDropItemEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (spigotRpg.isDisabledInWorld(e.getPlayer())) {
            return;
        }

        ActiveCharacter character = characterService.getCharacter(e.getPlayer());
        e.setCancelled(characterService.processUserAction(character, UserActionType.Q));

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (spigotRpg.isDisabledInWorld(e.getPlayer())) {
            return;
        }
        ActiveCharacter character = characterService.getCharacter(e.getPlayer().getUniqueId());
        e.setCancelled(characterService.processUserAction(character, UserActionType.E));

    }

}
