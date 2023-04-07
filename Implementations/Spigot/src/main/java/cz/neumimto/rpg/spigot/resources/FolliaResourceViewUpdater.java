package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

class FolliaResourceViewUpdater implements Listener {

    Consumer<SpigotCharacter> uiHandlerFactory;

    Map<UUID, ScheduledTask> tasks = new HashMap<>();

    public FolliaResourceViewUpdater(Consumer<SpigotCharacter> uiHandlerFactory) {
        this.uiHandlerFactory = uiHandlerFactory;
    }

    public void startForPlayer(SpigotCharacter character) {
        ScheduledTask scheduledTask1 = character.getEntity().getScheduler()
                .runAtFixedRate(SpigotRpgPlugin.getInstance(), scheduledTask -> {
                   uiHandlerFactory.accept(character);
        }, null, 0L, 50L);
        tasks.put(character.getUUID(), scheduledTask1);
    }

    public void stopForPlayer(SpigotCharacter character) {
        ScheduledTask remove = tasks.remove(character.getUUID());
        if (remove != null) {
            remove.cancel();
        }
    }


    @EventHandler
    public void onPlayerDisconnets(PlayerQuitEvent event) {

    }
}
