package cz.neumimto.rpg.spigot.gui.elements;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GuiCommand extends GuiItem {

    public GuiCommand(@NotNull ItemStack item, @NotNull String command, @NotNull CommandSender viewer) {
        super(item, e -> {
            e.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(),
                    () -> Bukkit.dispatchCommand(viewer, command), 1);
        });
    }

    public GuiCommand(@NotNull ItemStack item, @NotNull String command) {
        super(item, e -> {
            e.setCancelled(true);
            HumanEntity whoClicked = e.getWhoClicked();
            Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(),
                    () -> Bukkit.dispatchCommand(whoClicked, command), 1);
        });
    }

    public GuiCommand(@NotNull ItemStack item) {
        super(item, e -> {
            e.setCancelled(true);
        });
    }

    public GuiCommand(@NotNull ItemStack item, Consumer<InventoryClickEvent> listener) {
        super(item, listener);
    }
}
