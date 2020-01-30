package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.Bukkit;

import java.util.function.BiConsumer;

@JsBinding(JsBinding.Type.OBJECT)
@SkillComponent(
        value = "Puts a task into a scheduled execution",
        params = {
                @SkillComponent.Param("function - code to run later"),
                @SkillComponent.Param("delay - time in milliseconds")
        },
        usage = "delay(function() { ... }, delay)"
)
public class Delay implements BiConsumer<Runnable, Long> {
    @Override
    public void accept(Runnable r, Long l) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(), r, l);
    }
}
