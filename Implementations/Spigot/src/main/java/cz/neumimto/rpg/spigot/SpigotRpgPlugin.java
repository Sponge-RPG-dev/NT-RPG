package cz.neumimto.rpg.spigot;

import co.aikar.commands.*;
import cz.neumimto.FireworkHandler;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IRpgScriptEngine;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.common.commands.*;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.persistence.flatfiles.FlatFilesModule;
import cz.neumimto.rpg.spigot.bridges.HolographicDisplaysExpansion;
import cz.neumimto.rpg.spigot.bridges.MMOItemsExpansion;
import cz.neumimto.rpg.spigot.bridges.MythicalMobsExpansion;
import cz.neumimto.rpg.spigot.bridges.NtRpgPlaceholderExpansion;
import cz.neumimto.rpg.spigot.commands.SpigotAdminCommands;
import cz.neumimto.rpg.spigot.commands.SpigotCharacterCommands;
import cz.neumimto.rpg.spigot.commands.SpigotSkillBindCommands;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.configuration.SpigotMobSettingsDao;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpellbookListener;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import cz.neumimto.rpg.spigot.listeners.skillbinds.OnKeyPress;
import cz.neumimto.rpg.spigot.resources.SpigotGuiceModule;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Plugin(name = "NT-RPG", version = "2.1.0-SNAPSHOT-13")
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

    //Disable inventories due to nbtapi
    public boolean testEnv ;

    public SpigotRpgPlugin(){
        super();
    }

    protected SpigotRpgPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        if (dataFolder.getName().contains("Mock")) {
            testEnv = true;
        }
    }

    @Override
    public void onEnable() {
        Log.setLogger(logger);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        plugin = this;

        Path workingDirPath = getDataFolder().toPath();

        final BukkitScheduler scheduler = Bukkit.getScheduler();
        SpigotRpg spigotRpg = new SpigotRpg(workingDirPath.toString(), command -> scheduler.runTask(SpigotRpgPlugin.getInstance(), command));

        try {
            FireworkHandler.load(getClassLoader());
        } catch (Throwable t) {
            Log.warn("Unable to load Firework Handler");
        }
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

            IRpgScriptEngine scriptEngine = Rpg.get().getScriptEngine();
            scriptEngine.getDataToBind().put(EntityDamageEvent.DamageCause.class, JsBinding.Type.CLASS);
            scriptEngine.getDataToBind().put(EntityType.class, JsBinding.Type.CLASS);
            scriptEngine.getDataToBind().put(Particle.class, JsBinding.Type.CLASS);
            scriptEngine.getDataToBind().put(Color.class, JsBinding.Type.CLASS);

            if (Bukkit.getPluginManager().isPluginEnabled("EffectLib")) {
                scriptEngine.getDataToBind().put(AnimatedBallEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(ArcEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(AtomEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(BigBangEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(BleedEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(CircleEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(CloudEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(ColoredImageEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(ConeEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(CubeEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(CylinderEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(DiscoBallEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(DnaEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(DonutEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(DragonEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(EarthEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(EquationEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(ExplodeEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(FlameEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(FountainEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(GridEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(HeartEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(HelixEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(HillEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(IconEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(ImageEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(JumpEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(LineEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(LoveEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(ModifiedEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(MusicEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(PlotEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(ShieldEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(SkyRocketEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(SmokeEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(SphereEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(StarEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(TextEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(TornadoEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(TraceEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(TurnEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(VortexEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(WarpEffect.class, JsBinding.Type.CLASS);
                scriptEngine.getDataToBind().put(WaveEffect.class, JsBinding.Type.CLASS);
            }

            Rpg.get().registerListeners(injector.getInstance(OnKeyPress.class));
            if (!testEnv) {
                SpellbookListener.initBtns();
                SpigotGuiHelper.initInventories();
            }
        });


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
