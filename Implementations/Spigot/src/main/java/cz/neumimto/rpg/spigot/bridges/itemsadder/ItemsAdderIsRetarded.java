package cz.neumimto.rpg.spigot.bridges.itemsadder;


import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.commands.AdminCommands;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderIsRetarded implements Listener {

    @EventHandler
    public void onItemsAdderRetarded(ItemsAdderLoadDataEvent event) {
        if (event.getCause() == ItemsAdderLoadDataEvent.Cause.FIRST_LOAD) {
            SpigotRpgPlugin.getInstance().loadOrIAInstalled();
        } else if (event.getCause() == ItemsAdderLoadDataEvent.Cause.RELOAD){
            Rpg.get().getInjector().getInstance(AdminCommands.class).reload("a");
        }
    }
}
