package cz.neumimto.rpg.spigot;

import co.aikar.commands.*;
import com.comphenix.executors.BukkitExecutors;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.common.commands.*;
import cz.neumimto.rpg.persistence.flatfiles.FlatFilesModule;
import cz.neumimto.rpg.spigot.bridges.HolographicDisplaysExpansion;
import cz.neumimto.rpg.spigot.bridges.MMOItemsExpansion;
import cz.neumimto.rpg.spigot.bridges.MythicalMobsExpansion;
import cz.neumimto.rpg.spigot.bridges.NtRpgPlaceholderExpansion;
import cz.neumimto.rpg.spigot.commands.*;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.configuration.SpigotMobSettingsDao;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import cz.neumimto.rpg.spigot.listeners.SpigotItemCooldownListener;
import cz.neumimto.rpg.spigot.resources.SpigotGuiceModule;
import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Plugin(name = "NT-RPG", version = "2.1.0-SNAPSHOT-9")
@Description("Complete combat overhaul with classes and skills")
@Author("NeumimTo")
@Website("https://github.com/Sponge-RPG-dev/NT-RPG")
@LogPrefix("NTRPG")
@ApiVersion(ApiVersion.Target.v1_13)
@SoftDependsOn(
        value = {
                @SoftDependency("PlaceholderAPI"),
                @SoftDependency("HolographicDisplays"),
                @SoftDependency("MythicMobs"),
                @SoftDependency("MMOItems")
        }
)
@DependsOn(
        value = {
                @Dependency("NBTAPI"),
                @Dependency("EffectLib"),
                @Dependency("ProtocolLib")
        }
)
public class SpigotRpgPlugin extends JavaPlugin {

    private static SpigotRpgPlugin plugin;

    private static Logger logger = LoggerFactory.getLogger("NTRPG");
    private static EffectManager effectManager;

    public static SpigotRpgPlugin getInstance() {
        return plugin;
    }

    public final ExecutorService executor = Executors.newFixedThreadPool(5);
    ;

    @Override
    public void onEnable() {

        Log.setLogger(logger);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        plugin = this;

        Path workingDirPath = getDataFolder().toPath();

        SpigotRpg spigotRpg = new SpigotRpg(workingDirPath.toString(), BukkitExecutors.newSynchronous(this));


        CommandManager manager = new PaperCommandManager(this);

        manager.getCommandContexts().registerContext(OnlineOtherPlayer.class, c-> {
            CommandIssuer issuer = c.getIssuer();
            String lookup = c.popFirstArg();
            boolean allowMissing = c.isOptional();
            Player player = ACFBukkitUtil.findPlayerSmart(issuer, lookup);
            if (player == null) {
                if (allowMissing) {
                    return null;
                }
                throw new InvalidCommandArgument(false);
            }
            return new OnlineOtherPlayer(Rpg.get().getCharacterService().getCharacter(player.getUniqueId()));
        });

        spigotRpg.init(getDataFolder().toPath(), manager, new Class[]{
                SpigotAdminCommands.class,
                SpigotCharacterCommands.class,
                InfoCommands.class,
                PartyCommands.class,
                SkillCommands.class,
                ClassesComand.class,
                SkilltreeCommands.class,
                SpigotSkillBindCommands.class

        }, new FlatFilesModule(), (bindings, providers) -> new SpigotGuiceModule(this, spigotRpg, bindings, providers), injector -> {

            injector.injectMembers(spigotRpg);
            new RpgImpl(spigotRpg);

            injector.getInstance(Gui.class).setVanillaMessaging(injector.getInstance(SpigotGui.class));
            injector.getInstance(SpigotMobSettingsDao.class).load();

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                Log.info("PlaceholderAPI installed - registering NTRPG placeholders");
                injector.getInstance(NtRpgPlaceholderExpansion.class).register();
            }

            if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
                Log.info("HolographicDisplays installed - NTRPG will use it for some extra guis");
                HolographicDisplaysExpansion hde = injector.getInstance(HolographicDisplaysExpansion.class);
                hde.init();
                Bukkit.getPluginManager().registerEvents(hde, this);
            }

            if (Bukkit.getPluginManager().isPluginEnabled("MMOItems")) {
                Log.info("MMOItems installed - Provided hook for Power system and some stuff");
                MMOItemsExpansion mmie = injector.getInstance(MMOItemsExpansion.class);
                mmie.init(injector.getInstance(SpigotCharacterService.class));
                Bukkit.getPluginManager().registerEvents(mmie, this);
            }

            if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
                Log.info("MMOItems installed - Provided hook for Power system and some stuff");
                MythicalMobsExpansion mme = injector.getInstance(MythicalMobsExpansion.class);
                mme.init(injector.getInstance(SpigotEntityService.class));
                Bukkit.getPluginManager().registerEvents(mme, this);
            }

            IScriptEngine scriptEngine = Rpg.get().getScriptEngine();
            scriptEngine.getDataToBind().put(EntityDamageEvent.DamageCause.class, JsBinding.Type.CLASS);
            scriptEngine.getDataToBind().put(EntityType.class, JsBinding.Type.CLASS);

            if (Boolean.TRUE.equals(Rpg.get().getPluginConfig().ITEM_COOLDOWNS)) {
                Rpg.get().registerListeners(new SpigotItemCooldownListener());
            }
        });

        SpigotGuiHelper.initInventories();

        effectManager = new EffectManager(this);
        Rpg.get().getSyncExecutor();

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            Rpg.get().getCharacterService().loadPlayerData(onlinePlayer.getUniqueId(), onlinePlayer.getName());
        }

    }

    public static EffectManager getEffectManager() {
        return effectManager;
    }


    @Override
    public void onDisable() {
        executor.shutdown();
        CharacterService characterService = Rpg.get().getCharacterService();
        Collection<IActiveCharacter> characters = characterService.getCharacters();
        for (IActiveCharacter character : characters) {
            characterService.save(character.getCharacterBase());
        }
        effectManager.disposeOnTermination();
    }
}
