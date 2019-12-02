package cz.neumimto.rpg.spigot;

import cz.neumimto.rpg.common.AbstractRpg;
import cz.neumimto.rpg.common.assets.AssetService;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Singleton
public final class SpigotRpg extends AbstractRpg {

    @Inject
    private AssetService assetService;

    protected SpigotRpg(String workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }

    @Override
    public String getTextAssetContent(String templateName) {
        return assetService.getAssetAsString(templateName);
    }

    @Override
    public void executeCommandBatch(Map<String, String> args, List<String> enterCommands) {

    }

    @Override
    public boolean postEvent(Object event) {
        Bukkit.getServer().getPluginManager().callEvent((Event) event);
        if (event instanceof Cancellable) {
            return ((Cancellable) event).isCancelled();
        }
        return true;
    }

    @Override
    public void unregisterListeners(Object listener) {
        HandlerList.unregisterAll((Listener) listener);
    }

    @Override
    public void registerListeners(Object listener) {
        Bukkit.getServer().getPluginManager().registerEvents((Listener) listener, SpigotRpgPlugin.getInstance());
    }

    @Override
    public Executor getAsyncExecutor() {
        return null;
    }

    @Override
    public void scheduleSyncLater(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(), runnable);
    }

}
