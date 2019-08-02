package cz.neumimto.rpg.sponge;

import cz.neumimto.rpg.common.AbstractRpg;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Singleton
public final class SpongeRpg extends AbstractRpg {

    @Inject
    private VanillaMessaging vanillaMessaging;

    @Inject
    private ParticleDecorator particleDecorator;

    @Inject
    private RWService rwService;

    @Inject
    private NtRpgPlugin plugin;

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
        return NtRpgPlugin.asyncExecutor;
    }


    @Override
    public void postInit() {
        super.postInit();

        vanillaMessaging.load();
        particleDecorator.initModels();

        rwService.load();
    }

    @Override
    public void scheduleSyncLater(Runnable runnable) {
        Sponge.getScheduler().createTaskBuilder().execute(runnable).submit(NtRpgPlugin.getInstance());
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
