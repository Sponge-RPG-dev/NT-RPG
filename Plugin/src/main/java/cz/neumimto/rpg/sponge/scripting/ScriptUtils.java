package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.common.scripting.JsBinding;
import org.spongepowered.api.Sponge;

import java.util.concurrent.TimeUnit;

@JsBinding(JsBinding.Type.OBJECT)
public class ScriptUtils {

    public void runTaskWithDelay(Runnable runnable, int delay) {
        Sponge.getScheduler().createTaskBuilder().delay(delay, TimeUnit.MILLISECONDS).execute(runnable).submit(NtRpgPlugin.GlobalScope.plugin);
    }

}
