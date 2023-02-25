package cz.neumimto.rpg.spigot.gui.elements;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Icon extends GuiItem {
    public Icon(@NotNull ItemStack item) {
        super(item, inventoryClickEvent -> inventoryClickEvent.setCancelled(true), SpigotRpgPlugin.getInstance());
    }

}
