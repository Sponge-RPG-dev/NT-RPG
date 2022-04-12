package cz.neumimto.rpg.spigot.gui.inventoryviews;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

abstract class GuiHelper {

    static String t(String key) {
        return Rpg.get().getLocalizationService().translate(key);
    }

    static ItemStack i(GuiConfig.MaskConfig maskConfig) {
        ItemStack itemStack = DatapackManager.instance.findById(maskConfig.id);
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

    static ItemStack i(Material material, int model, ChatColor nameColor, String itemName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(nameColor + itemName);
        itemMeta.setCustomModelData(model);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
