package cz.neumimto.rpg.spigot;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Platform {

    private static boolean multithreading;

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionisedServer");
            multithreading = true;
        } catch (ClassNotFoundException e) {
            multithreading = false;
        }
    }

    public static boolean isFolia() {
        return multithreading;
    }

    public static Map cacheKV() {
        return createCache(Map.class);
    }

    public static <T> T createCache(Class<T> type) {
        if (type == Map.class) {
            if (isFolia()) {
                return (T) new ConcurrentHashMap<>();
            } else {
                return (T) new HashMap<>();
            }
        }
        throw new IllegalArgumentException(type + " cannot select type for ");
    }

    public static void schedule(Entity entity,) {
        Bukkit.getServer().getPlayer()
        entity.getScheduler().run(SpigotRpgPlugin.getInstance(), (Consumer<ScheduledTask>) scheduledTask -> {

        }, )
    }

}
