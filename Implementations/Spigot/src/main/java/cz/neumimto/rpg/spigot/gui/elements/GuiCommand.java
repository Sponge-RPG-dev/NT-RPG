package cz.neumimto.rpg.spigot.gui.elements;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuiCommand extends GuiItem {

    public GuiCommand(@NotNull ItemStack item, @NotNull String command, @NotNull CommandSender viewer) {
        super(item, e -> {
            e.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(),
                    () -> Bukkit.dispatchCommand(viewer, command), 1);
        });
    }
}
