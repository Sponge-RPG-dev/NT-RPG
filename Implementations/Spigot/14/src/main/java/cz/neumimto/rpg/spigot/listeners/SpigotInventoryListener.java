package cz.neumimto.rpg.spigot.listeners;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.ListenerClass
public class SpigotInventoryListener implements Listener {

    @Inject
    private SpigotCharacterService spigotCharacterService;

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            NBTItem nbti = new NBTItem(currentItem);
            if (nbti.hasKey("ntrpg.item-command")) {
                Rpg.get().scheduleSyncLater(() -> {
                    String command = nbti.getString("ntrpg.item-command");
                    Bukkit.dispatchCommand(whoClicked, command);
                });
                event.setResult(Event.Result.DENY);
            }
            if (nbti.hasKey("ntrpg.item-iface")) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
