package cz.neumimto.rpg.spigot;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.spigot.commands.SpigotAdminCommands;
import cz.neumimto.rpg.spigot.resources.SpigotGuiceModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.util.Collections;

@Plugin(name = "NT-RPG", version = "0.0.1-SNAPSHOT")
@Description("Complete combat overhaul with classes and skills")
@Author("NeumimTo")
@Website("https://github.com/Sponge-RPG-dev/NT-RPG")
@LogPrefix("NTRPG")
@ApiVersion(ApiVersion.Target.v1_13)
public class SpigotRpgPlugin extends JavaPlugin {

    private static SpigotRpgPlugin plugin;

    public static SpigotRpgPlugin getInstance() {
        return plugin;
    }


    @Override
    public void onEnable() {

        Injector injector = Guice.createInjector(new SpigotGuiceModule(this, Collections.emptyMap()));
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.getCommandContexts().registerIssuerAwareContext(IGlobalEffect.class, c -> {
            String s = c.popFirstArg();
            return Rpg.get().getEffectService().getGlobalEffect(s.toLowerCase());
        });
        manager.getCommandCompletions().registerAsyncCompletion("effect", c ->
            Rpg.get().getEffectService().getGlobalEffects().keySet()
        );
        manager.registerCommand(injector.getInstance(SpigotAdminCommands.class));


    }
}
