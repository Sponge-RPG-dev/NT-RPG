package cz.neumimto.rpg.spigot;

import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.common.AbstractRpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.info;

@Singleton
public final class SpigotRpg extends AbstractRpg {

    @Inject
    private AssetService assetService;

    protected SpigotRpg(String workingDirectory, Executor syncExecutor) {
        super(workingDirectory);
        super.currentThreadExecutor = syncExecutor;
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
        CommandSender commandSender = Bukkit.getConsoleSender();
        runCommands(args, enterCommands, commandSender);
    }

    @Override
    public void executeCommandAs(UUID sender, Map<String, String> args, List<String> enterCommands) {
        CommandSender player = Bukkit.getPlayer(sender);
        runCommands(args, enterCommands, player);
    }

    private void runCommands(Map<String, String> args, List<String> enterCommands, CommandSender commandSender) {
        for (String commandTemplate : enterCommands) {
            for (Map.Entry<String, String> entry : args.entrySet()) {
                commandTemplate = commandTemplate.replaceAll("\\{\\{" + entry.getKey() + "\\}\\}", entry.getValue());
            }
            try {
                info(Console.GREEN_BOLD + " Running Command (as a console): " + Console.YELLOW + commandTemplate);
                Bukkit.dispatchCommand(commandSender, commandTemplate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean postEvent(Object event) {
        Bukkit.getServer().getPluginManager().callEvent((Event) event);
        if (event instanceof Cancellable) {
            return ((Cancellable) event).isCancelled();
        }
        return false;
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
        return SpigotRpgPlugin.getInstance().executor;
    }

    @Override
    public void scheduleSyncLater(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(), runnable);
    }

    @Override
    public Set<UUID> getOnlinePlayers() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toSet());
    }

    @Override
    public void doImplSpecificreload() {
        SpigotGuiHelper.initInventories();
    }

    public boolean isDisabledInWorld(Entity entity) {
        return isDisabledInWorld(entity.getWorld());
    }

    public boolean isDisabledInWorld(Location location) {
        return isDisabledInWorld(location.getWorld());
    }

    public boolean isDisabledInWorld(World world) {
        return isDisabledInWorld(world.getName());
    }
}
