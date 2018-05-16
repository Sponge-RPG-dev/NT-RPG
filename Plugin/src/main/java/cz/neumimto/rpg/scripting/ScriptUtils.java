package cz.neumimto.rpg.scripting;

import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.Sponge;

import java.util.concurrent.TimeUnit;

@JsBinding(JsBinding.Type.OBJECT)
public class ScriptUtils {

    public void runTaskWithDelay(Runnable runnable, int delay) {
        Sponge.getScheduler().createTaskBuilder().delay(delay, TimeUnit.MILLISECONDS).execute(runnable).submit(NtRpgPlugin.GlobalScope.plugin);
    }

}
