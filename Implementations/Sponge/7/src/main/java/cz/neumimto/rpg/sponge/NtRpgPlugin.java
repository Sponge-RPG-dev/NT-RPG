/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.sponge;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.common.AbstractResourceManager;
import cz.neumimto.rpg.common.AddonScanner;
import cz.neumimto.rpg.sponge.commands.CommandService;
import cz.neumimto.rpg.sponge.configuration.Settings;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacter;
import cz.neumimto.rpg.sponge.inventory.data.*;
import cz.neumimto.rpg.sponge.inventory.data.manipulators.*;
import cz.neumimto.rpg.sponge.listeners.DebugListener;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.utils.Placeholders;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.service.sql.SqlService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 29.4.2015.
 */
@Plugin(id = "nt-rpg", version = "@VERSION@", name = "NT-Rpg", description = "RPG features for sponge", dependencies = {
        @Dependency(id = "placeholderapi", version = "4.5", optional = true)
})
@Resource
public class NtRpgPlugin extends Rpg {

    public static String workingDir;
    public static File pluginjar;

    public static SpongeExecutorService asyncExecutor;
    public static PluginConfig pluginConfig;;

    @Inject
    public Logger logger;

    @Inject
    private PluginContainer plugin;

    private static NtRpgPlugin instance;

    @Inject
    private Game game;

    @Inject
    private CauseStackManager causeStackManager;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path config;

    public static final Set<String> INTEGRATIONS = new HashSet<>();

    public Injector injector;

    @Listener
    public void initializeApi(GameConstructionEvent event) {
        long start = System.nanoTime();

        Log.setLogger(logger);
        try {
            workingDir = config.toString();
            URL url = FileUtils.getPluginUrl();
            pluginjar = new File(url.toURI());
        } catch (URISyntaxException us) {
            us.printStackTrace();
        }

        instance = this;
        asyncExecutor = Sponge.getGame().getScheduler().createAsyncExecutor(NtRpgPlugin.this);

        Game game = Sponge.getGame();
        Optional<PluginContainer> gui = game.getPluginManager().getPlugin("MinecraftGUIServer");
        if (gui.isPresent()) {

        } else {
            Settings.ENABLED_GUI = false;
        }

        Path path = Paths.get(workingDir);

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AddonScanner.setDeployedDir(Paths.get(workingDir, ".deployed"));
        AddonScanner.setAddonDir(Paths.get(workingDir, "addons"));
        AddonScanner.prepareAddons();
        Set<RpgAddon> rpgAddons = AbstractResourceManager.discoverGuiceModules();
        AddonScanner.onlyReloads();
        Map bindings = new HashMap<>();
        for (RpgAddon rpgAddon : rpgAddons) {
            bindings.putAll(rpgAddon.getBindings());
        }

        injector = Guice.createInjector(
                new SpongeGuiceModule(this, logger, game, causeStackManager, bindings)
        );
        super.impl = injector.getInstance(SpongeRpg.class);

        Rpg.get().reloadMainPluginConfig();

        Rpg.get().getResourceLoader().loadJarFile(pluginjar, true);
        Rpg.get().getResourceLoader().loadExternalJars();

        for (RpgAddon rpgAddon : rpgAddons) {
            rpgAddon.processStageEarly(injector);
        }

        Rpg.get().getResourceLoader().initializeComponents();



        if (pluginConfig.DEBUG.isBalance()) {
            Sponge.getEventManager().registerListeners(this, injector.getInstance(DebugListener.class));
        }
        CommandService commandService = injector.getInstance(CommandService.class);
        commandService.registerStandartCommands();


        if (INTEGRATIONS.contains("Placeholders")) {
            Placeholders placeholders = injector.getInstance(Placeholders.class);
            placeholders.init();
            info("Placeholders Initialized");
        }

        for (RpgAddon rpgAddon : rpgAddons) {
            rpgAddon.processStageLate(injector);
        }

        double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
        info("NtRpg plugin successfully loaded in " + elapsedTime + " seconds");
    }

    @Listener
    public void preinit(GamePreInitializationEvent e) {

        try {
            Class.forName("me.rojo8399.placeholderapi.PlaceholderService");
            INTEGRATIONS.add("Placeholders");
            info("Placeholders Enabled");
        } catch (ClassNotFoundException ignored) {
            info("Placeholders Disabled");
        }


        //Sponge.getEventManager().registerListeners(this, new PersistenceHandler());
        new NKeys();
        DataRegistration.builder()
                .manipulatorId("item_attribute_ref")
                .dataName("Attr Ref")
                .dataClass(AttributeRefMenuData.class)
                .immutableClass(AttributeRefMenuData.Immutable.class)
                .builder(new AttributeRefMenuData.AttributeRefMenuDataBuilder())
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .manipulatorId("custom_inventory_command")
                .dataName("Custom Inventory Command")
                .dataClass(InventoryCommandItemMenuData.class)
                .immutableClass(InventoryCommandItemMenuData.Immutable.class)
                .builder(new InventoryCommandItemMenuData.InventoryCommandItemMenuDataBuilder())
                .buildAndRegister(plugin);

        DataRegistration.<MenuInventoryData, MenuInventoryData.Immutable>builder()
                .manipulatorId("menu_inventory")
                .dataName("Menu Item")
                .dataClass(MenuInventoryData.class)
                .immutableClass(MenuInventoryData.Immutable.class)
                .builder(new MenuInventoryData.Builder())
                .buildAndRegister(plugin);


        DataRegistration.builder()
                .dataName("Item Effects")
                .manipulatorId("item_effects")
                .dataClass(EffectsData.class)
                .immutableClass(EffectsData.Immutable.class)
                .builder(new EffectsData.EffectDataBuilder())
                .buildAndRegister(plugin);


        DataRegistration.<ItemAttributesData, ItemAttributesData.Immutable>builder()
                .dataClass(ItemAttributesData.class)
                .immutableClass(ItemAttributesData.Immutable.class)
                .builder(new ItemAttributesData.Builder())
                .manipulatorId("ntrpg-itemattributes")
                .dataName("ItemAttributesData")
                .buildAndRegister(plugin);

        DataRegistration.<ItemLevelData, ItemLevelData.Immutable>builder()
                .dataClass(ItemLevelData.class)
                .immutableClass(ItemLevelData.Immutable.class)
                .builder(new ItemLevelData.Builder())
                .manipulatorId("ntrpg-itemleveldata")
                .dataName("ItemLevelData")
                .buildAndRegister(plugin);

        DataRegistration.<ItemRarityData, ItemRarityData.Immutable>builder()
                .dataClass(ItemRarityData.class)
                .immutableClass(ItemRarityData.Immutable.class)
                .builder(new ItemRarityData.Builder())
                .manipulatorId("ntrpg-rarity")
                .dataName("ItemRarityData")
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .dataName("Item Sockets")
                .manipulatorId("item_sockets")
                .dataClass(ItemSocketsData.class)
                .immutableClass(ItemSocketsData.Immutable.class)
                .builder(new ItemSocketsData.Builder())
                .buildAndRegister(plugin);


        DataRegistration.<LoreDamageData, LoreDamageData.Immutable>builder()
                .dataClass(LoreDamageData.class)
                .immutableClass(LoreDamageData.Immutable.class)
                .builder(new LoreDamageData.Builder())
                .manipulatorId("ntrpg-loredamage")
                .dataName("LoreDamageData")
                .buildAndRegister(plugin);

        DataRegistration.<LoreDurabilityData, LoreDurabilityData.Immutable>builder()
                .dataClass(LoreDurabilityData.class)
                .immutableClass(LoreDurabilityData.Immutable.class)
                .builder(new LoreDurabilityData.Builder())
                .manipulatorId("ntrpg-loredurability")
                .dataName("LoreDurabilityData")
                .buildAndRegister(plugin);

        DataRegistration.<MinimalItemRequirementsData, MinimalItemRequirementsData.Immutable>builder()
                .dataClass(MinimalItemRequirementsData.class)
                .immutableClass(MinimalItemRequirementsData.Immutable.class)
                .builder(new MinimalItemRequirementsData.Builder())
                .manipulatorId("ntrpg-minimalrequirements")
                .dataName("MinimalItemRequirementsData")
                .buildAndRegister(plugin);

        DataRegistration.<SectionDelimiterData, SectionDelimiterData.Immutable>builder()
                .dataClass(SectionDelimiterData.class)
                .immutableClass(SectionDelimiterData.Immutable.class)
                .builder(new SectionDelimiterData.Builder())
                .manipulatorId("ntrpg-sectiondelimiter")
                .dataName("SectionDelimiterData")
                .buildAndRegister(plugin);

        DataRegistration.<ItemStackUpgradeData, ItemStackUpgradeData.Immutable>builder()
                .manipulatorId("itemstack_upgrade")
                .dataName("ItemStack Upgrade")
                .dataClass(ItemStackUpgradeData.class)
                .immutableClass(ItemStackUpgradeData.Immutable.class)
                .builder(new ItemStackUpgradeData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.<SkillTreeInventoryViewControllsData, SkillTreeInventoryViewControllsData.Immutable>builder()
                .manipulatorId("skilltree_controlls")
                .dataName("SkillTree Controll Buttons")
                .dataClass(SkillTreeInventoryViewControllsData.class)
                .immutableClass(SkillTreeInventoryViewControllsData.Immutable.class)
                .builder(new SkillTreeInventoryViewControllsData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.<SkillTreeNode, SkillTreeNode.Immutable>builder()
                .manipulatorId("skilltree_node")
                .dataName("SkillTree Node")
                .dataClass(SkillTreeNode.class)
                .immutableClass(SkillTreeNode.Immutable.class)
                .builder(new SkillTreeNode.Builder())
                .buildAndRegister(plugin);

        DataRegistration.<ItemMetaHeader, ItemMetaHeader.Immutable>builder()
                .manipulatorId("item_type_data")
                .dataName("Item Type data")
                .dataClass(ItemMetaHeader.class)
                .immutableClass(ItemMetaHeader.Immutable.class)
                .builder(new ItemMetaHeader.Builder())
                .buildAndRegister(plugin);


        DataRegistration.<MinimalItemGroupRequirementsData, MinimalItemGroupRequirementsData.Immutable>builder()
                .manipulatorId("item_minimal_group_requirements")
                .dataName("Item group requirements")
                .dataClass(MinimalItemGroupRequirementsData.class)
                .immutableClass(MinimalItemGroupRequirementsData.Immutable.class)
                .builder(new MinimalItemGroupRequirementsData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.<ItemMetaTypeData, ItemMetaTypeData.Immutable>builder()
                .manipulatorId("item_meta_type")
                .dataName("Item meta type")
                .dataClass(ItemMetaTypeData.class)
                .immutableClass(ItemMetaTypeData.Immutable.class)
                .builder(new ItemMetaTypeData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.<ItemSubtypeData, ItemSubtypeData.Immutable>builder()
                .manipulatorId("item_meta_subtype")
                .dataName("Item meta subtype")
                .dataClass(ItemSubtypeData.class)
                .immutableClass(ItemSubtypeData.Immutable.class)
                .builder(new ItemSubtypeData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.<SkillBindData, SkillBindData.Immutable>builder()
                .manipulatorId("skill_bind")
                .dataName("SkillBind")
                .dataClass(SkillBindData.class)
                .immutableClass(SkillBindData.Immutable.class)
                .builder(new SkillBindData.Builder())
                .buildAndRegister(plugin);

    }

    @Listener
    public void postInit7(GameRegistryEvent.Register<DamageType> event) {
        event.register(NDamageType.FIRE);
        event.register(NDamageType.ICE);
        event.register(NDamageType.LIGHTNING);
        event.register(NDamageType.DAMAGE_CHECK);
    }

    public static NtRpgPlugin getInstance() {
        return instance;
    }

}