package cz.neumimto.rpg.spigot.gui.inventoryviews;

import cz.neumimto.rpg.api.Rpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

abstract class GuiHelper {

    static Inventory createInventory(InventoryHolder viewer, String preferedColor, String traslationName) {
        ChatColor c = ChatColor.WHITE;
        if (preferedColor != null) {
            c = ChatColor.valueOf(preferedColor.toUpperCase());
        }
        String translate = Rpg.get().getLocalizationService().translate(traslationName);
        return Bukkit.createInventory(viewer, 6 * 9, c + translate);
    }

    static String t(String key) {
        return Rpg.get().getLocalizationService().translate(key);
    }

    static ItemStack i(GuiConfig.MaskConfig maskConfig) {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(maskConfig.id));
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (maskConfig.model != null) {
            itemMeta.setCustomModelData(maskConfig.model);
        }
        if (maskConfig.translationKey != null) {
            itemMeta.setDisplayName(Rpg.get().getLocalizationService().translate(maskConfig.translationKey));
        } else {
            itemMeta.setDisplayName(" ");
        }
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static ItemStack i(Material material, Integer model) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (model != null) {
            itemMeta.setCustomModelData(model);
        }
        itemMeta.setDisplayName(" ");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static ItemStack i(String material, String model) {
       return i(Material.matchMaterial(material), model == null ? null : Integer.parseInt(model));
    }

    static ItemStack i(Material material, int model, ChatColor nameColor, String itemName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(nameColor + itemName);
        itemMeta.setCustomModelData(model);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
