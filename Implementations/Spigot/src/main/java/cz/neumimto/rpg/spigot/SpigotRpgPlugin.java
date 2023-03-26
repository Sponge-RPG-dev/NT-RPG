package cz.neumimto.rpg.spigot;

import co.aikar.commands.*;
import com.google.inject.Injector;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.commands.*;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.persistence.flatfiles.FlatFilesModule;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import cz.neumimto.rpg.spigot.bridges.NtRpgPlaceholderExpansion;
import cz.neumimto.rpg.spigot.bridges.denizen.DenizenHook;
import cz.neumimto.rpg.spigot.bridges.itemsadder.ItemsAdderHook;
import cz.neumimto.rpg.spigot.bridges.itemsadder.ItemsAdderIsRetarded;
import cz.neumimto.rpg.spigot.bridges.luckperms.LuckpermsExpansion;
import cz.neumimto.rpg.spigot.bridges.mimic.MimicHook;
import cz.neumimto.rpg.spigot.bridges.mmoitems.MMOItemsExpansion;
import cz.neumimto.rpg.spigot.bridges.mythicalmobs.MythicalMobsExpansion;
import cz.neumimto.rpg.spigot.bridges.oraxen.OraxenHook;
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
import cz.neumimto.rpg.spigot.resources.SpigotGuiceModuleBuilder;
import de.slikey.effectlib.EffectManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class SpigotRpgPlugin extends JavaPlugin implements Listener {

    private static SpigotRpgPlugin plugin;

    private static EffectManager effectManager;

    public static SpigotRpgPlugin getInstance() {
        return plugin;
    }

    public static final ExecutorService executor = Executors.newFixedThreadPool(5);

    //Disable inventories due to nbtapi
    public static boolean testEnv;
    private File dataFolder;
    private static Injector injector;

    public static List<String> activeHooks = new ArrayList<>();

    public SpigotRpgPlugin() {
        super();
    }

    protected SpigotRpgPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public static Injector getInjector() {
        return injector;
    }

    private static BukkitAudiences bukkitAudiences;

    public static BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    private ItemsAdderIsRetarded iaCallbackListener/*?*/;

    @Override
    public void onEnable() {

        Log.setLogger(getLogger());

        ascii();

        List<BaseCommand> commandClasses = Stream.of(
                        new SpigotAdminCommands(),
                        new AdminCommands(),
                        new CharacterCommands(),
                        new CastCommand(),
                        new SpigotCharacterCommands(),
                        new InfoCommands(),
                        new PartyCommands(),
                        new SkillCommands(),
                        new ClassesComand(),
                        new SkilltreeCommands(),
                        new SpigotSkillBindCommands())
                .toList();

        plugin = this;
        bukkitAudiences = BukkitAudiences.create(getInstance());
        dataFolder = getDataFolder();
        CommandManager manager = new PaperCommandManager(this);
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

        ACFBootstrap.initializeACF(manager, commandClasses);

        effectManager = new EffectManager(getInstance());

        Bukkit.getPluginManager().registerEvents(this, this);


        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        Path workingDirPath = getDataFolder().toPath();

        final BukkitScheduler scheduler = Bukkit.getScheduler();
        SpigotRpg spigotRpg = new SpigotRpg(workingDirPath.toString(), command -> scheduler.runTask(SpigotRpgPlugin.getInstance(), command));

        spigotRpg.init(getDataFolder().toPath(), null, commandClasses, new FlatFilesModule(), (bindings, providers) -> new SpigotGuiceModuleBuilder().setNtRpgPlugin(this).setSpigotRpg(spigotRpg).setExtraBindings(bindings).setProviders(providers).setMinecraftVersion(Bukkit.getServer().getMinecraftVersion()).createSpigotGuiceModule(),
                injector -> {

                    SpigotRpgPlugin.injector = injector;
                    injector.injectMembers(spigotRpg);
                    new RpgImpl(spigotRpg);

                    injector.getInstance(DatapackManager.class).init();

                    for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                        initThirdpartyHooks(pl.getName());
                    }

                    injector.getInstance(Gui.class).setVanillaMessaging(injector.getInstance(SpigotGui.class));
                    injector.getInstance(SpigotMobSettingsDao.class).load();


                    Rpg.get().registerListeners(injector.getInstance(OnKeyPress.class));
                    PacketHandler.init();
                    new SpigotSkillTreeViewModel(); //just to call static block

                });

        /*
          ItemsAdder.getAllItems returns null, despite plugin being already enabled at this point.
        */
        Plugin itemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        if (itemsAdder != null && itemsAdder.isEnabled() && iaCallbackListener == null) {
            iaCallbackListener = new ItemsAdderIsRetarded();
            getLogger().info("Itemsadder found, ntrpg has to postpone initialization until ia loads all items.");
            Bukkit.getPluginManager().registerEvents(iaCallbackListener, this);
            return;
        }

        loadOrIAInstalled();

    }

    public void loadOrIAInstalled() {

        Rpg.get().initServices();

        Rpg.get().doImplSpecificreload();

        if (!testEnv) {
            Resourcepack.init();
            SpellbookListener.initBtns();
            SpigotGuiHelper.initInventories();
        }

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            Rpg.get().getCharacterService().loadPlayerData(onlinePlayer.getUniqueId(), onlinePlayer.getName());
        }
    }

    @EventHandler
    public void pluginEnabledEvent(PluginEnableEvent event) {
        initThirdpartyHooks(event.getPlugin().getName());
    }

    public void initThirdpartyHooks(String pluginName) {
        initSafely(pluginName, "PlaceholderAPI", () -> {
            Log.info("PlaceholderAPI installed - registering NTRPG placeholders");
            injector.getInstance(NtRpgPlaceholderExpansion.class).register();
        });

        initSafely(pluginName, "MMOItems", () -> {
            Log.info("MMOItems installed - Provided hook for Power system and some stuff");
            MMOItemsExpansion mmie = injector.getInstance(MMOItemsExpansion.class);
            mmie.init(injector.getInstance(SpigotCharacterService.class));
            Bukkit.getPluginManager().registerEvents(mmie, getInstance());
        });

        initSafely(pluginName, "MythicMobs", () -> {
            Log.info("MythicMobs installed - Provided hook for Power system and some stuff");
            MythicalMobsExpansion mme = injector.getInstance(MythicalMobsExpansion.class);
            mme.init(injector.getInstance(SpigotEntityService.class));
            Bukkit.getPluginManager().registerEvents(mme, getInstance());
        });

        initSafely(pluginName, "RPGRegions", () -> {
            Log.info("RPGRegions installed - registering experience extension");
            RpgRegionsClassExpReward.init();
        });

        initSafely(pluginName, "Mimic", () -> {
            Log.info("Mimic installed - registering level and class systems");
            MimicHook mimicHook = injector.getInstance(MimicHook.class);
            mimicHook.init(plugin);
        });

        initSafely(pluginName, "Denizen", () -> {
            Log.info("Denizen installed - enabling denizen skill scripting extension");
            DenizenHook.init(plugin);
        });

        initSafely(pluginName, "Oraxen", () -> {
            Log.info("Oraxen installed - any oraxen item can be accessed from ntrpg configs using format 'oraxen:my_custom_item'");
            injector.getInstance(OraxenHook.class).init();
        });

        initSafely(pluginName, "ItemsAdder", () -> {
            Log.info("ItemsAdder installed - any ia item can be accessed from ntrpg configs using format 'itemsadder:my_custom_item'");
            injector.getInstance(ItemsAdderHook.class).init();
        });

        initSafely(pluginName, "LuckPerms", () -> {
            Log.info("LuckPerms installed - registering additional permission contexts'");
            injector.getInstance(LuckpermsExpansion.class).init();
        });
    }

    public static void initSafely(String pluginName, String name, Runnable r) {
        if (!pluginName.equalsIgnoreCase(name)) {
            return;
        }
        if (Rpg.get().getPluginConfig().DISABLED_HOOKS
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet())
                .contains(name.toLowerCase())) {
            return;
        }
        if (activeHooks.contains(pluginName)) {
            return;
        }
        try {
            r.run();
            activeHooks.add(pluginName);
        } catch (Throwable t) {
            Log.error("Unable to hook into " + name, t);
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

    private static void ascii() {
        Function<String, Component> colorInput = s -> {
            TextComponent.Builder builder = text();
            for (char c : s.toCharArray()) {
                switch (c) {
                    case ' ':
                        builder.append(text(" "));
                        break;
                    case '█':
                        builder.append(text(c).color(GOLD));
                        break;
                    default:
                        builder.append(text(c).color(DARK_GRAY));
                }
            }
            return builder.build();
        };

        Bukkit.getConsoleSender().sendMessage(empty());
        Bukkit.getConsoleSender().sendMessage(colorInput.apply(" ███╗   ████████████████╗██████╗ ██████╗ "));
        Bukkit.getConsoleSender().sendMessage(colorInput.apply(" ████╗  ██╚══██╔══██╔══████╔══████╔════╝ "));
        Bukkit.getConsoleSender().sendMessage(colorInput.apply(" ██╔██╗ ██║  ██║  ██████╔██████╔██║  ███╗"));
        Bukkit.getConsoleSender().sendMessage(colorInput.apply(" ██║╚██╗██║  ██║  ██╔══████╔═══╝██║   ██║"));
        Bukkit.getConsoleSender().sendMessage(colorInput.apply(" ██║ ╚████║  ██║  ██║  ████║    ╚██████╔╝"));
        Bukkit.getConsoleSender().sendMessage(colorInput.apply(" ╚═╝  ╚═══╝  ╚═╝  ╚═╝  ╚═╚═╝     ╚═════╝ "));
        Bukkit.getConsoleSender().sendMessage(empty());
    }
}
