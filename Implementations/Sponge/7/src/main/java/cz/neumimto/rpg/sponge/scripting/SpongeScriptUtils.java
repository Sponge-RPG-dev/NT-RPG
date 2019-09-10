package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import org.spongepowered.api.Sponge;

import java.util.concurrent.TimeUnit;

@JsBinding(JsBinding.Type.OBJECT)
public class SpongeScriptUtils {

    public void runTaskWithDelay(Runnable runnable, int delay) {
        Sponge.getScheduler().createTaskBuilder().delay(delay, TimeUnit.MILLISECONDS).execute(runnable).submit(SpongeRpgPlugin.getInstance());
    }

}
