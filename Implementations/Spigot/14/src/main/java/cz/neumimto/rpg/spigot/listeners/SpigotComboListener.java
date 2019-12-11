package cz.neumimto.rpg.spigot.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.UserActionType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Inject;

@Singleton
@ResourceLoader.ListenerClass
public class SpigotComboListener implements Listener {

    @Inject
    private SpigotCharacterService characterService;

    @EventHandler
    public void onRMBClick(PlayerInteractEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            IActiveCharacter character = characterService.getCharacter(e.getPlayer());
            e.setCancelled(characterService.processUserAction(character, UserActionType.R));
        }
    }

    @EventHandler
    public void onLMBClick(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            IActiveCharacter character = characterService.getCharacter(e.getPlayer());
            e.setCancelled(characterService.processUserAction(character, UserActionType.L));
        }
    }

    @EventHandler
    public void onQPress(PlayerDropItemEvent  e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            IActiveCharacter character = characterService.getCharacter(e.getPlayer());
            e.setCancelled(characterService.processUserAction(character, UserActionType.Q));
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            IActiveCharacter character = characterService.getCharacter(e.getPlayer().getUniqueId());
            e.setCancelled(characterService.processUserAction(character, UserActionType.E));
        }
    }

}
