package cz.neumimto.rpg.spigot;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Platform {

    private static boolean multithreading;

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
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


    public static void timer(Runnable despawn) {
        if  (multithreading) {
            Bukkit.getServer().getAsyncScheduler().runAtFixedRate(SpigotRpgPlugin.getInstance(), scheduledTask -> despawn.run(), 0L, 50, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(SpigotRpgPlugin.getInstance(), despawn, 0L, 20);
        }
    }
}
