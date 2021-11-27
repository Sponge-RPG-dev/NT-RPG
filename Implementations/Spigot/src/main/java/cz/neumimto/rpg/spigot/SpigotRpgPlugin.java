package cz.neumimto.rpg.spigot;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.CommandManager;
import co.aikar.commands.InvalidCommandArgument;
import com.google.auto.service.AutoService;
import com.google.inject.Injector;
import cz.neumimto.rpg.NtRpgBootstrap;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.commands.*;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.persistence.flatfiles.FlatFilesModule;
import cz.neumimto.rpg.spigot.bridges.HolographicDisplaysExpansion;
import cz.neumimto.rpg.spigot.bridges.NtRpgPlaceholderExpansion;
import cz.neumimto.rpg.spigot.bridges.mmoitems.MMOItemsExpansion;
import cz.neumimto.rpg.spigot.bridges.mythicalmobs.MythicalMobsExpansion;
import cz.neumimto.rpg.spigot.bridges.rpgregions.RpgRegionsClassExpReward;
import cz.neumimto.rpg.spigot.commands.SpigotAdminCommands;
import cz.neumimto.rpg.spigot.commands.SpigotCharacterCommands;
import cz.neumimto.rpg.spigot.commands.SpigotSkillBindCommands;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.configuration.SpigotMobSettingsDao;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpellbookListener;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import cz.neumimto.rpg.spigot.gui.SpigotSkillTreeViewModel;
import cz.neumimto.rpg.spigot.listeners.skillbinds.OnKeyPress;
import cz.neumimto.rpg.spigot.packetwrapper.PacketHandler;
import cz.neumimto.rpg.spigot.resources.SpigotGuiceModule;
import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AutoService(NtRpgBootstrap.class)
public class SpigotRpgPlugin implements NtRpgBootstrap {

    private static JavaPlugin plugin;

    private static EffectManager effectManager;

    public static JavaPlugin getInstance() {
        return plugin;
    }

    public static final ExecutorService executor = Executors.newFixedThreadPool(5);

    //Disable inventories due to nbtapi
    public static boolean testEnv;
    private File dataFolder;
    private static Injector injector;

    public static Injector getInjector() {
        return injector;
    }

    @NotNull
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public void enable(Data data) {
        plugin = (JavaPlugin) data.plugin();
        dataFolder = data.workingDir();

        Log.setLogger(data.logger());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        Path workingDirPath = getDataFolder().toPath();

        final BukkitScheduler scheduler = Bukkit.getScheduler();
        SpigotRpg spigotRpg = new SpigotRpg(workingDirPath.toString(), command -> scheduler.runTask(SpigotRpgPlugin.getInstance(), command));

        CommandManager manager = data.commandManager();

        manager.getCommandContexts().registerContext(OnlineOtherPlayer.class, c -> {
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
                AdminCommands.class,
                CharacterCommands.class,
                CastCommand.class,
                SpigotCharacterCommands.class,
                InfoCommands.class,
                PartyCommands.class,
                SkillCommands.class,
                ClassesComand.class,
                SkilltreeCommands.class,
                SpigotSkillBindCommands.class

        }, new FlatFilesModule(), (bindings, providers) -> new SpigotGuiceModule(this, spigotRpg, bindings, providers), injector -> {

            SpigotRpgPlugin.injector = injector;
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
                Bukkit.getPluginManager().registerEvents(hde, getInstance());
            }

            if (Bukkit.getPluginManager().isPluginEnabled("MMOItems")) {
                Log.info("MMOItems installed - Provided hook for Power system and some stuff");
                MMOItemsExpansion mmie = injector.getInstance(MMOItemsExpansion.class);
                mmie.init(injector.getInstance(SpigotCharacterService.class));
                Bukkit.getPluginManager().registerEvents(mmie, getInstance());
            }

            if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
                Log.info("MMOItems installed - Provided hook for Power system and some stuff");
                MythicalMobsExpansion mme = injector.getInstance(MythicalMobsExpansion.class);
                mme.init(injector.getInstance(SpigotEntityService.class));
                Bukkit.getPluginManager().registerEvents(mme, getInstance());
            }

            if (Bukkit.getPluginManager().isPluginEnabled("RPGRegions")) {
                Log.info("RPGRegions installed - registering experience extension");
                RpgRegionsClassExpReward.init();
            }

            Rpg.get().registerListeners(injector.getInstance(OnKeyPress.class));
            PacketHandler.init();
            new SpigotSkillTreeViewModel(); //just to call static block
        });

        if (!testEnv) {
            Resourcepack.init();
            SpellbookListener.initBtns();
            SpigotGuiHelper.initInventories();
        }

        effectManager = new EffectManager(getInstance());
        Rpg.get().getSyncExecutor();

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            Rpg.get().getCharacterService().loadPlayerData(onlinePlayer.getUniqueId(), onlinePlayer.getName());
        }

    }

    public static EffectManager getEffectManager() {
        return effectManager;
    }


    public void disable() {
        executor.shutdown();
        CharacterService<IActiveCharacter> characterService = Rpg.get().getCharacterService();
        Collection<IActiveCharacter> characters = characterService.getCharacters();
        for (IActiveCharacter character : characters) {
            if (character instanceof PreloadCharacter) {
                continue;
            }
            characterService.save(character.getCharacterBase());
        }
        if (getEffectManager() != null) {
            getEffectManager().disposeOnTermination();
        }
    }
}
