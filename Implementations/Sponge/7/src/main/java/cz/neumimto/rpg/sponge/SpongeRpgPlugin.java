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

import co.aikar.commands.ACFSpongeUtil;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.SpongeCommandManager;
import com.google.inject.Inject;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.common.commands.*;
import cz.neumimto.rpg.persistence.flatfiles.FlatFilesModule;
import cz.neumimto.rpg.sponge.bridges.HologramsListener;
import cz.neumimto.rpg.sponge.bridges.Placeholders;
import cz.neumimto.rpg.sponge.commands.SpongeAdminCommands;
import cz.neumimto.rpg.sponge.commands.SpongeCharacterCommands;
import cz.neumimto.rpg.sponge.commands.SpongeCommandService;
import cz.neumimto.rpg.sponge.commands.SpongeSkillBindCommands;
import cz.neumimto.rpg.sponge.gui.GuiHelper;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.inventory.data.*;
import cz.neumimto.rpg.sponge.inventory.data.manipulators.*;
import cz.neumimto.rpg.sponge.listeners.SpongeItemCooldownListener;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 29.4.2015.
 */
@Plugin(id = "nt-rpg", version = "2.1.0-SNAPSHOT-1", name = "NT-Rpg", description = "RPG features for sponge", dependencies = {
        @Dependency(id = "placeholderapi", version = "4.5", optional = true),
        @Dependency(id = "Holograms", optional = true)
})
@Resource
public class SpongeRpgPlugin extends Rpg {

    public static final Set<String> INTEGRATIONS = new HashSet<>();
    public static String workingDir;
    public static File pluginjar;
    public static SpongeExecutorService asyncExecutor;
    public static PluginConfig pluginConfig;
    private static SpongeRpgPlugin instance;
    public Injector injector; // Sponge injector ?
    @Inject
    private Logger logger;
    @Inject
    private PluginContainer plugin;
    @Inject
    private CauseStackManager causeStackManager;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path config;

    public static SpongeRpgPlugin getInstance() {
        return instance;
    }

    @Listener
    public void initializeApi(GamePreInitializationEvent event) {
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
        asyncExecutor = Sponge.getScheduler().createAsyncExecutor(this);

        Game game = Sponge.getGame();

        Path workingDirPath = Paths.get(workingDir);

        try {
            Files.createDirectories(workingDirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginContainer pluginContainer = Sponge.getPluginManager().fromInstance(this).get();

        /*
        try {
            Class.forName("me.rojo8399.placeholderapi.PlaceholderService");
            INTEGRATIONS.add("Placeholders");
            info("Placeholders Enabled");
        } catch (ClassNotFoundException ignored) {
            info("Placeholders Disabled");
        }

        if (INTEGRATIONS.contains("Placeholders")) {
            Placeholders placeholders = injector.getInstance(Placeholders.class); //What injector?
            placeholders.load();
            info("Placeholders Initialized");
        }*/

        //Sponge.getEventManager().registerListeners(this, new PersistenceHandler());
        new NKeys();
        DataRegistration.builder()
                .manipulatorId("item_attribute_ref")
                .dataName("Attr Ref")
                .dataClass(AttributeRefMenuData.class)
                .immutableClass(AttributeRefMenuData.Immutable.class)
                .builder(new AttributeRefMenuData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .manipulatorId("custom_inventory_command")
                .dataName("Custom Inventory Command")
                .dataClass(InventoryCommandItemMenuData.class)
                .immutableClass(InventoryCommandItemMenuData.Immutable.class)
                .builder(new InventoryCommandItemMenuData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.<MenuInventoryData, MenuInventoryData.Immutable>builder()
                .manipulatorId("menu_inventory")
                .dataName("Menu Item")
                .dataClass(MenuInventoryData.class)
                .immutableClass(MenuInventoryData.Immutable.class)
                .builder(new MenuInventoryData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .manipulatorId("custom_inventory_action")
                .dataName("Custom Inventory Action")
                .dataClass(InventoryActionItemMenuData.class)
                .immutableClass(InventoryActionItemMenuData.Immutable.class)
                .builder(new InventoryActionItemMenuData.Builder())
                .buildAndRegister(plugin);

        DataRegistration.builder()
                .dataName("Item Effects")
                .manipulatorId("item_effects")
                .dataClass(EffectsData.class)
                .immutableClass(EffectsData.Immutable.class)
                .builder(new EffectsData.Builder())
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

        double elapsedTime = (System.nanoTime() - start) / 1000000000.0;


        SpongeCommandManager manager = new SpongeCommandManager(pluginContainer);

        manager.getCommandContexts().registerContext(OnlineOtherPlayer.class, c -> {
            CommandIssuer issuer = c.getIssuer();
            String lookup = c.popFirstArg();
            boolean allowMissing = c.isOptional();
            Player player = ACFSpongeUtil.findPlayerSmart(issuer, lookup);
            if (player == null) {
                if (allowMissing) {
                    return null;
                }
                throw new InvalidCommandArgument(false);
            }
            return new OnlineOtherPlayer(Rpg.get().getCharacterService().getCharacter(player.getUniqueId()));
        });

        impl = new SpongeRpg(workingDir, Sponge.getScheduler().createSyncExecutor(this));

        impl.init(
                workingDirPath,
                manager,
                new Class[]{
                        SpongeAdminCommands.class,
                        AdminCommands.class,
                        CharacterCommands.class,
                        CastCommand.class,
                        SpongeCharacterCommands.class,
                        InfoCommands.class,
                        PartyCommands.class,
                        SkillCommands.class,
                        ClassesComand.class,
                        SkilltreeCommands.class,
                        SpongeSkillBindCommands.class
                },
                new FlatFilesModule(),
                (bindings, providers) -> new SpongeGuiceModule(this, logger, game, causeStackManager, bindings),
                injector -> {
                    injector.injectMembers(impl);

                    SpongeCommandService commandService = injector.getInstance(SpongeCommandService.class);
                    commandService.registerStandartCommands();

                    injector.getInstance(Gui.class).setVanillaMessaging(injector.getInstance(VanillaMessaging.class));

                    if (Boolean.TRUE.equals(Rpg.get().getPluginConfig().ITEM_COOLDOWNS)) {
                        Rpg.get().registerListeners(new SpongeItemCooldownListener());
                    }


                    Sponge.getPluginManager().getPlugin("placeholderapi").ifPresent(a -> {
                        injector.getInstance(Placeholders.class).init();
                    });

                    try {
                        Class.forName("de.randombyte.holograms.api.class.HologramsService");
                        injector.getInstance(HologramsListener.class).init();
                        Log.info("Holograms inetgartions enabled");
                    } catch (ClassNotFoundException e) {

                    }

                }
        );
        Rpg.get().getSyncExecutor();


        info("NtRpg plugin successfully loaded in " + elapsedTime + " seconds");
    }


    @Listener
    public void gameStartedEvent(GameStartedServerEvent event) {
        GuiHelper.initInventories();
    }

    @Listener
    public void postInit7(GameRegistryEvent.Register<DamageType> event) {
        event.register(NDamageType.FIRE);
        event.register(NDamageType.ICE);
        event.register(NDamageType.LIGHTNING);
    }

}
