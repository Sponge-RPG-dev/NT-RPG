package cz.neumimto.rpg.sponge;

import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.common.AbstractRpg;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class SpongeRpg extends AbstractRpg {

    @Inject
    private VanillaMessaging vanillaMessaging;

    @Inject
    private ParticleDecorator particleDecorator;

    @Inject
    private RWService rwService;

    @Inject
    private SpongeRpgPlugin plugin;

    protected SpongeRpg(String workingDir) {
        super(workingDir);
    }

    private void broadcastMessage(Text text) {
        Sponge.getServer().getBroadcastChannel().send(text);
    }

    @Override
    public void broadcastMessage(String message) {
        broadcastMessage(TextHelper.parse(message));
    }

    @Override
    public String getTextAssetContent(String templateName) {
        try {
            return Sponge.getAssetManager().getAsset(plugin, templateName).get().readString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unknown template " + templateName);
        }
    }

    @Override
    public void executeCommandBatch(Map<String, String> args, List<String> cmd) {
        Utils.executeCommandBatch(args, cmd);
    }

    @Override
    public boolean postEvent(Object event) {
        return Sponge.getEventManager().post((Event) event);
    }

    @Override
    public void unregisterListeners(Object listener) {
        Sponge.getEventManager().unregisterListeners(listener);
    }

    @Override
    public void registerListeners(Object listener) {
        Sponge.getEventManager().registerListeners(plugin, listener);
    }

    @Override
    public Executor getAsyncExecutor() {
        return SpongeRpgPlugin.asyncExecutor;
    }

    @Override
    public void init(Path workingDirPath, Object commandManager, Class[] commandClasses, RpgAddon defaultStorageImpl, BiFunction<Map, Map<Class<?>, ?>, Module> fnInjProv, Consumer<Injector> injectorc) {
        super.init(workingDirPath, commandManager, commandClasses, defaultStorageImpl, fnInjProv, injectorc);

        vanillaMessaging.load();
        particleDecorator.initModels();

        rwService.load();
    }

    @Override
    public void scheduleSyncLater(Runnable runnable) {
        Sponge.getScheduler().createTaskBuilder().delayTicks(1).execute(runnable).submit(SpongeRpgPlugin.getInstance());
    }

    public VanillaMessaging getVanillaMessaging() {
        return vanillaMessaging;
    }

    public ParticleDecorator getParticleDecorator() {
        return particleDecorator;
    }

    public RWService getRwService() {
        return rwService;
    }
}
