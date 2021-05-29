package cz.neumimto.rpg.spigot.gui.inventoryviews;

import cz.neumimto.rpg.api.Rpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

abstract class GuiHelper {

    static Inventory createInventory(InventoryHolder viewer, String traslationName) {
        ChatColor c = ChatColor.WHITE;
        if (preferedColor != null) {
            c = ChatColor.valueOf(preferedColor.toUpperCase());
        }
        String translate = Rpg.get().getLocalizationService().translate(traslationName);
        return Bukkit.createInventory(viewer, 6 * 9, c + translate);
    }
}
