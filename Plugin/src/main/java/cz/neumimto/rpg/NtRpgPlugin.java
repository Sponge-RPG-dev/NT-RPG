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

package cz.neumimto.rpg;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import cz.neumimto.configuration.ConfigMapper;
import cz.neumimto.core.PluginCore;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.LocalizationService;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.commands.AnyClassDefCommandElement;
import cz.neumimto.rpg.commands.CharacterAttributeCommandElement;
import cz.neumimto.rpg.commands.ClassDefCommandElement;
import cz.neumimto.rpg.commands.ClassTypeCommandElement;
import cz.neumimto.rpg.commands.GlobalEffectCommandElement;
import cz.neumimto.rpg.commands.LearnedSkillCommandElement;
import cz.neumimto.rpg.commands.PartyMemberCommandElement;
import cz.neumimto.rpg.commands.PlayerClassCommandElement;
import cz.neumimto.rpg.commands.RuneCommandElement;
import cz.neumimto.rpg.commands.UnlearnedSkillCommandElement;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.configuration.Settings;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import cz.neumimto.rpg.inventory.data.InventoryCommandItemMenuData;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.SkillTreeInventoryViewControllsData;
import cz.neumimto.rpg.inventory.data.manipulators.EffectsData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemAttributesData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemLevelData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemMetaHeader;
import cz.neumimto.rpg.inventory.data.manipulators.ItemMetaTypeData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemRarityData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemSocketsData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemStackUpgradeData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemSubtypeData;
import cz.neumimto.rpg.inventory.data.manipulators.LoreDamageData;
import cz.neumimto.rpg.inventory.data.manipulators.LoreDurabilityData;
import cz.neumimto.rpg.inventory.data.manipulators.MinimalItemGroupRequirementsData;
import cz.neumimto.rpg.inventory.data.manipulators.MinimalItemRequirementsData;
import cz.neumimto.rpg.inventory.data.manipulators.SectionDelimiterData;
import cz.neumimto.rpg.inventory.data.manipulators.SkillBindData;
import cz.neumimto.rpg.inventory.data.manipulators.SkillTreeNode;
import cz.neumimto.rpg.inventory.items.ItemMetaType;
import cz.neumimto.rpg.inventory.items.ItemMetaTypeRegistry;
import cz.neumimto.rpg.inventory.items.ItemMetaTypes;
import cz.neumimto.rpg.inventory.items.subtypes.ItemSubtype;
import cz.neumimto.rpg.inventory.items.subtypes.ItemSubtypeRegistry;
import cz.neumimto.rpg.inventory.items.subtypes.ItemSubtypes;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.inventory.slotparsers.DefaultPlayerInvHandler;
import cz.neumimto.rpg.inventory.slotparsers.PlayerInvHandler;
import cz.neumimto.rpg.inventory.slotparsers.PlayerInvHandlerRegistry;
import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.inventory.sockets.SocketTypeRegistry;
import cz.neumimto.rpg.inventory.sockets.SocketTypes;
import cz.neumimto.rpg.listeners.DebugListener;
import cz.neumimto.rpg.persistance.ClassDefinitionDao;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExperienceSource;
import cz.neumimto.rpg.players.ExperienceSourceRegistry;
import cz.neumimto.rpg.players.ExperienceSources;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.PreloadCharacter;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.players.properties.attributes.AttributeRegistry;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.ISkillType;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.SkillTypeRegistry;
import cz.neumimto.rpg.skills.configs.SkillConfigLoader;
import cz.neumimto.rpg.skills.configs.SkillConfigLoaderRegistry;
import cz.neumimto.rpg.skills.configs.SkillConfigLoaders;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactoryRegistry;
import cz.neumimto.rpg.skills.mods.SkillPreprocessorFactories;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.parents.IActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.utils.EditorMappings;
import cz.neumimto.rpg.utils.FileUtils;
import cz.neumimto.rpg.utils.Placeholders;
import cz.neumimto.rpg.utils.TriConsumer;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.Resource;

import static cz.neumimto.rpg.Log.info;
import static cz.neumimto.rpg.Log.warn;

/**
 * Created by NeumimTo on 29.4.2015.
 */
@Plugin(id = "nt-rpg", version = "@VERSION@", name = "NT-Rpg", description = "RPG features for sponge", dependencies = {
        @Dependency(id = "nt-core", version = "1.13-SNAPSHOT-6"),
        @Dependency(id = "placeholderapi", version = "4.5", optional = true)
})
@Resource
public class NtRpgPlugin {

    public static String workingDir;
    public static File pluginjar;
    public static GlobalScope GlobalScope;
    public static SpongeExecutorService asyncExecutor;
    public static PluginConfig pluginConfig;

    @Inject
    public Logger logger;

    @Inject
    PluginContainer plugin;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path config;


    @Listener
    public void preinit(GamePreInitializationEvent e) {
        PluginCore.MANAGED_JPA_TYPES.add(CharacterBase.class);
        PluginCore.MANAGED_JPA_TYPES.add(BaseCharacterAttribute.class);
        PluginCore.MANAGED_JPA_TYPES.add(CharacterSkill.class);
        PluginCore.MANAGED_JPA_TYPES.add(CharacterClass.class);
        Sponge.getEventManager().registerListeners(this, new PersistenceHandler(this));
        new NKeys();
        DataRegistration.<InventoryCommandItemMenuData, InventoryCommandItemMenuData.Immutable>builder()
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

        Sponge.getRegistry().registerModule(SocketType.class, new SocketTypeRegistry());
        Sponge.getRegistry().registerModule(ICharacterAttribute.class, new AttributeRegistry());
        Sponge.getRegistry().registerModule(PlayerInvHandler.class, new PlayerInvHandlerRegistry());
        Sponge.getRegistry().registerModule(ItemMetaType.class, new ItemMetaTypeRegistry());
        Sponge.getRegistry().registerModule(ItemSubtype.class, new ItemSubtypeRegistry());
        Sponge.getRegistry().registerModule(ISkillType.class, new SkillTypeRegistry());
        Sponge.getRegistry().registerModule(ExperienceSource.class, new ExperienceSourceRegistry());
        Sponge.getRegistry().registerModule(SkillConfigLoader.class, new SkillConfigLoaderRegistry());
        Sponge.getRegistry().registerModule(SkillPreProcessorFactory.class, new SkillPreProcessorFactoryRegistry());
    }

    @Listener
    public void postInit(GameRegistryEvent.Register<SocketType> event) {
        event.register(SocketTypes.ANY);
        event.register(SocketTypes.GEM);
        event.register(SocketTypes.JEWEL);
        event.register(SocketTypes.RUNE);
    }

    @Listener
    public void postInit1(GameRegistryEvent.Register<PlayerInvHandler> event) {
        event.register(new DefaultPlayerInvHandler());
    }

    @Listener
    public void postInit2(GameRegistryEvent.Register<ItemMetaType> event) {
        event.register(ItemMetaTypes.CHARM);
        event.register(ItemMetaTypes.RUNEWORD);
    }

    @Listener
    public void postInit3(GameRegistryEvent.Register<ItemSubtype> event) {
        event.register(ItemSubtypes.ANY);
    }

    @Listener
    public void postInit4(GameRegistryEvent.Register<ISkillType> event) {
        for (SkillType skillType : SkillType.values()) {
            event.register(skillType);
        }
    }

    @Listener
    public void postInit5(GameRegistryEvent.Register<SkillConfigLoader> event) {
        event.register(SkillConfigLoaders.ATTRIBUTE);
        event.register(SkillConfigLoaders.ITEM_ACCESS);
        event.register(SkillConfigLoaders.PROPERTY);
        event.register(SkillConfigLoaders.SKILLTREE_PATH);
    }

    @Listener
    public void postInit6(GameRegistryEvent.Register<ExperienceSource> event) {
        event.register(ExperienceSources.PVP);
        event.register(ExperienceSources.PVE);
        event.register(ExperienceSources.MINING);
        event.register(ExperienceSources.LOGGING);
        event.register(ExperienceSources.QUESTING);
        event.register(ExperienceSources.FISHING);
        event.register(ExperienceSources.FARMING);
    }

    @Listener
    public void postInit7(GameRegistryEvent.Register<DamageType> event) {
        event.register(NDamageType.DAMAGE_CHECK);
        event.register(NDamageType.ICE);
        event.register(NDamageType.LIGHTNING);
        event.register(NDamageType.MEELE_CRITICAL);
    }

    @Listener
    public void postInit8(GameRegistryEvent.Register<SkillPreProcessorFactory> event) {
        event.register(SkillPreprocessorFactories.UNCASTABLE);
        event.register(SkillPreprocessorFactories.ADJUSTED_SKILL_SETTINGS);
    }

    @Listener
    public void onPluginLoad(GamePostInitializationEvent event) {
        long start = System.nanoTime();
        Log.logger = logger;
        try {
            workingDir = config.toString();
            URL url = FileUtils.getPluginUrl();
            pluginjar = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        reloadMainPluigonConfig();
        IoC ioc = IoC.get();
        asyncExecutor = Sponge.getGame().getScheduler().createAsyncExecutor(NtRpgPlugin.this);

        ioc.registerInterfaceImplementation(Logger.class, logger);
        Game game = Sponge.getGame();
        Optional<PluginContainer> gui = game.getPluginManager().getPlugin("MinecraftGUIServer");
        if (gui.isPresent()) {
            //ioc.registerInterfaceImplementation(MinecraftGuiService.class, game.getServiceManager().provide(MinecraftGuiService.class).get());
        } else {
            Settings.ENABLED_GUI = false;
        }
        ioc.registerDependency(this);
        ioc.registerInterfaceImplementation(CauseStackManager.class, Sponge.getCauseStackManager());

        Path path = Paths.get(workingDir);
        ConfigMapper.init("NtRpg", path);
        ioc.registerDependency(ConfigMapper.get("NtRpg"));
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ioc.get(IoC.class, ioc);
        ResourceLoader rl = ioc.build(ResourceLoader.class);
        rl.loadJarFile(pluginjar, true);
        GlobalScope = ioc.build(GlobalScope.class);
        rl.loadExternalJars();

        if (pluginConfig.DEBUG.isBalance()) {
            Sponge.getEventManager().registerListeners(this, ioc.build(DebugListener.class));
        }
        registerCommands();
        IoC.get().build(LocalizationService.class).registerClass(Localizations.class);
        IoC.get().build(LocalizationService.class)
                .loadResourceBundle("assets.nt-rpg.localizations.localization", Locale.forLanguageTag(pluginConfig.LOCALE), null);
        IoC.get().build(Init.class).it();


        Sponge.getRegistry().registerModule(ISkill.class, IoC.get().build(SkillService.class));

        try {
            Class.forName("me.rojo8399.placeholderapi.PlaceholderService");
            Placeholders build = IoC.get().build(Placeholders.class);
            build.init();
            info("Placeholders Enabled");
        } catch (ClassNotFoundException e) {
            info("Placeholders Disabled");
        }

        ioc.postProcess();

        EditorMappings.dump();
        double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
        info("NtRpg plugin successfully loaded in " + elapsedTime + " seconds");
    }

    private void reloadMainPluigonConfig() {
        File file = new File(NtRpgPlugin.workingDir);
        if (!file.exists()) {
            file.mkdir();
        }
        File properties = new File(NtRpgPlugin.workingDir, "Settings.conf");
        if (!properties.exists()) {
            FileUtils.generateConfigFile(new PluginConfig(), properties);
        }
        try {
            ObjectMapper<PluginConfig> mapper = ObjectMapper.forClass(PluginConfig.class);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(properties.toPath()).build();
            pluginConfig = mapper.bind(new PluginConfig()).populate(hcl.load());
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    public void registerCommands() {
        registerAdminCommands();
        registerCharacterCommands();
        registerSkillCommands();
    }


    public void registerAdminCommands() {
        // ===========================================================
        // ==================         SKILLS        ==================
        // ===========================================================
        CommandSpec executeSkill = CommandSpec.builder()
                .description(TextSerializers
                        .FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_ADMIN_EXEC_SKILL_DESC))
                .arguments(
                        GenericArguments.catalogedElement(Text.of("skill"), ISkill.class),
                        GenericArguments.flags().valueFlag(GenericArguments
                                .integer(TextHelper.parse("level")), "l")
                                .buildWith(GenericArguments.none())
                )
                .executor((src, args) -> {
                    ISkill skill = args.<ISkill>getOne("skill").get();
                    SkillSettings defaultSkillSettings = skill.getSettings();
                    Player player = (Player) src;
                    IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
                    if (character.isStub()) {
                        throw new RuntimeException("Character is required even for an admin.");
                    }

                    int level = 1;
                    Optional<Integer> optional = args.getOne("level");
                    if (optional.isPresent()) {
                        level = optional.get();
                    }
                    if (skill instanceof ActiveSkill) {
                        Long l = System.nanoTime();

                        PlayerSkillContext playerSkillContext = new PlayerSkillContext(null, skill);
                        playerSkillContext.setLevel(level);
                        SkillData skillData = new SkillData(skill.getId());
                        skillData.setSkillSettings(defaultSkillSettings);
                        playerSkillContext.setSkillData(skillData);
                        playerSkillContext.setSkill(skill);
                        ActiveSkill askill = (ActiveSkill) skill;
                        SkillContext skillContext = new SkillContext((IActiveSkill) skill, playerSkillContext) {{
                            wrappers.add(new SkillExecutorCallback() {
                                @Override
                                public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                                    Long e = System.nanoTime();
                                    character.getPlayer().sendMessage(Text.of("Exec Time: " + TimeUnit.MILLISECONDS.convert(e - l, TimeUnit.NANOSECONDS)));
                                }
                            });
                        }};
                        skillContext.sort();
                        askill.cast(character, playerSkillContext, skillContext);
                    }
                    return CommandResult.success();
                })
                .build();

        // ===========================================================
        // ==================        ITEM       ==================
        // ===========================================================

        Gson gson = new Gson();
        CommandSpec enchantAdd = CommandSpec.builder()
                .description(TextSerializers
                        .FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_ADMIN_ENCHANT_ADD))
                .arguments(
                        new GlobalEffectCommandElement(TextHelper.parse("effect")),
                        GenericArguments.remainingJoinedStrings(TextHelper.parse("params"))
                )
                .executor((src, args) -> {
                    IGlobalEffect effect = args.<IGlobalEffect>getOne("effect").get();
                    Player player = (Player) src;
                    if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                        ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();

                        Optional<String> params = args.getOne("params");
                        String s = params.get();
                        try {
                            if (s.equals("?")) {
                                Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
                                if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                                    player.sendMessage(Text.of("Expected: " + modelType.getTypeName()));
                                } else {
                                    Map<String, String> q = new HashMap<>();
                                    for (Field field : modelType.getDeclaredFields()) {
                                        q.put(field.getName(), field.getType().getName());
                                    }
                                    player.sendMessage(Text.of("Expected: " + gson.toJson(q)));
                                }
                            } else {
                                EffectParams map = null;
                                Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
                                if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                                    map = new EffectParams();
                                    map.put(effect.asEffectClass().getName(), s);
                                } else {
                                    map = gson.fromJson(s, EffectParams.class);
                                }
                                itemStack = GlobalScope.inventorySerivce.addEffectsToItemStack(itemStack, effect.getName(), map);
                                itemStack = GlobalScope.inventorySerivce.updateLore(itemStack);
                                player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
                                player.sendMessage(TextHelper.parse("Enchantment " + effect.getName() + " added"));
                            }
                        } catch (JsonSyntaxException e) {
                            Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
                            Map<String, String> q = new HashMap<>();
                            for (Field field : modelType.getDeclaredFields()) {
                                q.put(field.getName(), field.getType().getName());
                            }
                            throw new RuntimeException("Expected: " + gson.toJson(q));
                        }
                    } else {
                        player.sendMessage(Localizations.NO_ITEM_IN_HAND.toText());
                    }
                    return CommandResult.success();
                })
                .build();


        CommandSpec enchant = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_ENCHANT))
                .child(enchantAdd, "add", "e")
                .build();

        // ===========================================================
        // ==================         SOCKET        ==================
        // ===========================================================

        CommandSpec socket = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_SOCKET))
                .arguments(
                        GenericArguments.catalogedElement(Text.of("type"), SocketType.class)
                )
                .executor((src, args) -> {
                    Player player = (Player) src;
                    Optional<SocketType> type = args.getOne("type");
                    if (type.isPresent()) {
                        Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
                        if (itemInHand.isPresent()) {
                            ItemStack itemStack = NtRpgPlugin.GlobalScope.runewordService.createSocket(itemInHand.get(), type.get());
                            player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
                            return CommandResult.builder().affectedItems(1).build();
                        }
                        src.sendMessage(Localizations.NO_ITEM_IN_HAND.toText());
                        return CommandResult.empty();
                    }
                    return CommandResult.empty();
                })
                .build();

        // ===========================================================
        // ==================          RUNE         ==================
        // ===========================================================

        CommandSpec rune = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_RUNE))
                .arguments(
                        new RuneCommandElement(TextHelper.parse("rune"))
                )
                .executor((src, args) -> {
                    Rune runee = args.<Rune>getOne("rune").get();
                    Player player = (Player) src;

                    ItemStack is = NtRpgPlugin.GlobalScope.runewordService.createRune(SocketTypes.RUNE, runee.getName());
                    player.getInventory().offer(is);
                    return CommandResult.success();
                })
                .build();

        // ===========================================================
        // =================          RARITY        ==================
        // ===========================================================

        CommandSpec rarity = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_RARITY))
                .arguments(
                        GenericArguments.integer(TextHelper.parse("level"))
                )
                .executor((src, args) -> {
                    Integer integer = args.<Integer>getOne("level").get();
                    Set<Integer> i = new HashSet<>();
                    for (String s : pluginConfig.ITEM_RARITY) {
                        i.add(Integer.parseInt(s.split(",")[0]));
                    }
                    if (!i.contains(integer)) {
                        src.sendMessage(Text.builder("Unknown rarity value").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }


                    Player player = (Player) src;

                    Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
                    if (!itemInHand.isPresent()) {
                        src.sendMessage(Text.builder("No item in main hand").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }

                    ItemStack itemStack = itemInHand.get();

                    GlobalScope.inventorySerivce.setItemRarity(itemInHand.get(), integer);
                    GlobalScope.inventorySerivce.createItemMetaSectionIfMissing(itemStack);
                    GlobalScope.inventorySerivce.updateLore(itemStack);
                    player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
                    return CommandResult.success();
                })
                .build();

        // ===========================================================
        // =================          META        ==================
        // ===========================================================

        CommandSpec meta = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_RARITY))
                .arguments(
                        GenericArguments.text(Text.of("meta"), TextSerializers.FORMATTING_CODE, true)
                )
                .executor((src, args) -> {
                    Text meta1 = args.<Text>getOne("meta").get();
                    Player player = (Player) src;
                    Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
                    if (!itemInHand.isPresent()) {
                        src.sendMessage(Text.builder("No item in main hand").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }
                    ItemStack itemStack = itemInHand.get();

                    GlobalScope.inventorySerivce.createItemMeta(itemStack, meta1);
                    GlobalScope.inventorySerivce.updateLore(itemStack);
                    player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
                    return CommandResult.success();
                })
                .build();
        // ===========================================================
        // ==================    ITEM type  ==================
        // ===========================================================
        CommandSpec mt = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_ITEM_TYPE))
                .arguments(
                        GenericArguments.catalogedElement(Text.of("type"), ItemMetaType.class)
                )
                .executor((src, args) -> {
                    Player player = (Player) src;
                    Optional<ItemMetaType> type = args.getOne("type");
                    if (type.isPresent()) {
                        Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
                        if (itemInHand.isPresent()) {
                            ItemStack itemStack = itemInHand.get();
                            GlobalScope.inventorySerivce.createItemMetaSectionIfMissing(itemStack);
                            GlobalScope.inventorySerivce.setItemMetaType(itemStack, type.get());
                            GlobalScope.inventorySerivce.updateLore(itemStack);
                            player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
                            return CommandResult.builder().affectedItems(1).build();
                        }
                        src.sendMessage(Localizations.NO_ITEM_IN_HAND.toText());
                        return CommandResult.empty();
                    }
                    return CommandResult.empty();
                })
                .build();

        // ===========================================================
        // ==================    ITEM restrictions  ==================
        // ===========================================================

        CommandSpec rst = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_RARITY))
                .arguments(
                        new ClassDefCommandElement(Text.of("group")),
                        GenericArguments.integer(Text.of("level"))
                )
                .executor((src, args) -> {

                    Player player = (Player) src;
                    Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
                    if (!itemInHand.isPresent()) {
                        src.sendMessage(Text.builder("No item in main hand").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }
                    ItemStack itemStack = itemInHand.get();
                    ClassDefinition group = args.<ClassDefinition>getOne("group").get();
                    Integer integer = args.<Integer>getOne("level").orElse(0);

                    GlobalScope.inventorySerivce.addGroupRestriction(itemStack, group, integer);
                    GlobalScope.inventorySerivce.updateLore(itemStack);
                    player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
                    return CommandResult.success();
                })
                .build();


        // ===========================================================
        // ==================           RW          ==================
        // ===========================================================

        CommandSpec runeword = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_RUNEWORD))
                .arguments(
                        new RuneCommandElement(TextHelper.parse("rw"))
                )
                .executor((src, args) -> {
                    RuneWord r = args.<RuneWord>getOne("rw").get();
                    Player p = (Player) src;
                    Optional<ItemStack> itemInHand = p.getItemInHand(HandTypes.MAIN_HAND);
                    if (itemInHand.isPresent()) {
                        ItemStack itemStack = itemInHand.get();
                        ItemStack itemStack1 = NtRpgPlugin.GlobalScope.runewordService.reBuildRuneword(itemStack, r);
                        p.setItemInHand(HandTypes.MAIN_HAND, itemStack1);
                    }
                    return CommandResult.success();
                })
                .build();

        // ===========================================================
        // ==================          EXP          ==================
        // ===========================================================

        CommandSpec expadd = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_EXP_ADD))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player"))),
                        GenericArguments.remainingJoinedStrings(TextHelper.parse("data"))
                )
                .executor((src, args) -> {
                    Player player = args.<Player>getOne("player").get();
                    String data = args.<String>getOne("data").get();
                    IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
                    Collection<PlayerClassData> classes = character.getClasses().values();
                    String[] a = data.split(" ");
                    for (PlayerClassData aClass : classes) {
                        if (aClass.getClassDefinition().getName().equalsIgnoreCase(a[0])) {
                            if (aClass.takesExp()) {
                                NtRpgPlugin.GlobalScope.characterService.addExperiences(character, Double.valueOf(a[1]), aClass);
                            }
                        }
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec exp = CommandSpec.builder()
                .child(expadd, "add")
                .build();


        // ===========================================================
        // ==================          ROOT         ==================
        // ===========================================================

        CommandSpec reload = CommandSpec.builder()
                .description(TextSerializers
                        .FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_ADMIN_RELOAD))
                .arguments(GenericArguments.remainingJoinedStrings(TextHelper.parse("args")))
                .executor((src, args) -> {
                    String[] a = args.<String>getOne("args").get().split(" ");
                    if (a[0].equalsIgnoreCase("js")) {
                        if (!(pluginConfig.DEBUG.isBalance())) {
                            src.sendMessage(TextHelper.parse("Reloading is allowed only in debug mode"));
                            return CommandResult.success();
                        }
                        JSLoader jsLoader = IoC.get().build(JSLoader.class);
                        jsLoader.initEngine();

                        int i = 1;
                        String q = null;
                        while (i < a.length) {
                            q = a[i];
                            if (q.equalsIgnoreCase("skills") || q.equalsIgnoreCase("s")) {
                                jsLoader.reloadSkills();
                                CharacterService build = IoC.get().build(CharacterService.class);
                                SkillService skillService = IoC.get().build(SkillService.class);
                                build.getCharacters()
                                        .stream()
                                        .forEach(qw -> {
                                            Map<String, PlayerSkillContext> skills = qw.getSkills();
                                            for (Map.Entry<String, PlayerSkillContext> entry : skills.entrySet()) {
                                                if (entry.getValue() == PlayerSkillContext.Empty) {
                                                    continue;
                                                }
                                                PlayerSkillContext value = entry.getValue();
                                                Optional<ISkill> byId = skillService.getById(value.getSkill().getId());
                                                if (!byId.isPresent()) {
                                                    throw new RuntimeException("Unabled to reload the skill " + value.getSkill().getId() + ". "
                                                            + "Restart the server");
                                                }
                                                ISkill skill = byId.get();
                                                value.setSkill(skill);
                                                value.getSkillData().setSkill(skill);
                                            }
                                        });
                            }
                            if (q.equalsIgnoreCase("attributes") || q.equalsIgnoreCase("a")) {
                                jsLoader.reloadAttributes();
                            }
                            if (q.equalsIgnoreCase("globaleffects") || q.equalsIgnoreCase("g")) {
                                jsLoader.reloadGlobalEffects();
                            }
                            i++;
                        }
                    } else if (a[0].equalsIgnoreCase("skilltree")) {
                        IoC.get().build(SkillService.class).reloadSkillTrees();
                    } else if (a[0].equalsIgnoreCase("settings")) {
                        reloadMainPluigonConfig();
                    } else if (a[0].equalsIgnoreCase("mobs")) {
                        IoC.get().build(EntityService.class).reloadMobConfiguration();
                    } else if (a[0].equalsIgnoreCase("classes")) {
                        //Check if configs are ok
                        warn("[RELOAD] Attempting to reload classes from config files...");
                        info("[RELOAD] Checking class files: ");
                        ClassDefinitionDao build = IoC.get().build(ClassDefinitionDao.class);
                        try {
                            build.parseClassFiles();
                            info("[RELOAD] Class files ok");

                            //Get all objects we need to save
                            Set<CharacterBase> characterBases = new HashSet<>();
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
                                if (character.isStub()){
                                    continue;
                                }
                                characterBases.add(character.getCharacterBase());
                            }

                            //Set Char stubs
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
                                if (character.isStub()){
                                    continue;
                                }
                                PreloadCharacter preloadCharacter = GlobalScope.characterService.buildDummyChar(player.getUniqueId());
                                GlobalScope.characterService.registerDummyChar(preloadCharacter);
                            }
                            logger.info("[RELOAD] Purging effect caches");
                            GlobalScope.effectService.purgeEffectCache();
                            GlobalScope.effectService.stop();
                            //todo purge all skills
                            //todo purge all skilltrees

                            for (CharacterBase characterBase : characterBases) {
                                logger.info("[RELOAD] saving character " + characterBase.getLastKnownPlayerName());
                                GlobalScope.characterService.save(characterBase);
                            }


                            //we should be ready to start loading stuff back
                            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                //System.gc(); - for reloading skills its required

                                GlobalScope.effectService.start();
                                //todo load skills
                                //todo load skilltrees

                                GlobalScope.classService.loadClasses();
                                Comparator<CharacterBase> cmp = Comparator.comparing(CharacterBase::getUpdated);
                                for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                    List<CharacterBase> playersCharacters =
                                            GlobalScope.characterService.getPlayersCharacters(player.getUniqueId());
                                    if (playersCharacters.isEmpty()) {
                                        continue;
                                    }
                                    CharacterBase max = playersCharacters.stream().max(cmp).get();
                                    ActiveCharacter activeCharacter = GlobalScope.characterService.createActiveCharacter(player.getUniqueId(), max);
                                    GlobalScope.characterService.setActiveCharacter(player.getUniqueId(), activeCharacter);
                                    GlobalScope.characterService.invalidateCaches(activeCharacter);
                                    GlobalScope.characterService.assignPlayerToCharacter(player);
                                }
                            }).submit(this);



                        } catch (ObjectMappingException e) {
                            src.sendMessage(Text.of("Errors occured during class reload, check server console for more informations"));
                        }
                    } else {
                        src.sendMessage(TextHelper.parse("js[s/a/g] skilltree [r,a] classes"));
                        return CommandResult.empty();
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec effect = CommandSpec.builder()
                .description(
                        TextSerializers
                                .FORMATTING_CODE
                                .deserialize(CommandLocalization.COMMAND_ADMIN_EFFECT_ADD))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player"))),
                        new GlobalEffectCommandElement(Text.of("effect")),
                        GenericArguments.longNum(TextHelper.parse("duration")),
                        GenericArguments.remainingJoinedStrings(TextHelper.parse("data"))
                )
                .executor((src, args) -> {
                    Player player = args.<Player>getOne("player").get();
                    String data = args.<String>getOne("data").get();
                    Long k = args.<Long>getOne("duration").get();
                    IGlobalEffect effect1 = args.<IGlobalEffect>getOne("data").get();
                    IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
                    EffectParams params = gson.fromJson(data, EffectParams.class);
                    GlobalScope.effectService.addEffect(effect1.construct(character, k, params), character, InternalEffectSourceProvider.INSTANCE);
                    return CommandResult.success();
                })
                .build();

        final TriConsumer<CommandSource, String, Player> PROPERTY_DETAIL = (src, data, player) -> {
            PropertyService ps = IoC.get().build(PropertyService.class);
            CharacterService cs = IoC.get().build(CharacterService.class);
            try {
                int idByName = ps.getIdByName(data);
                IActiveCharacter character = cs.getCharacter(player);
                src.sendMessage(Text.of(TextColors.GOLD, "=================="));
                src.sendMessage(Text.of(TextColors.GREEN, data));

                src.sendMessage(Text.of(TextColors.GOLD, "Value", TextColors.WHITE, "/",
                        TextColors.AQUA, "Effective Value", TextColors.WHITE, "/",
                        TextColors.GRAY, "Cap",
                        TextColors.DARK_GRAY, " .##"));

                NumberFormat formatter = new DecimalFormat("#0.00");
                src.sendMessage(Text.of(TextColors.GOLD, formatter.format(character.getProperty(idByName)), TextColors.WHITE, "/",
                        TextColors.AQUA, formatter.format(cs.getCharacterProperty(character, idByName)), TextColors.WHITE, "/",
                        TextColors.GRAY, formatter.format(ps.getMaxPropertyValue(idByName))));

                src.sendMessage(Text.of(TextColors.GOLD, "=================="));
                src.sendMessage(Text.of(TextColors.GRAY, "Memory/1 player: " + (character.getPrimaryProperties().length * 2 * 4) / 1024.0 + "kb"));

            } catch (Throwable t) {
                src.sendMessage(Text.of("No such property"));
            }
        };

        CommandSpec inspectProperty = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player"))),
                        GenericArguments.remainingJoinedStrings(TextHelper.parse("data"))
                )
                .executor((src, args) -> {
                    Player player = args.<Player>getOne("player").get();
                    String data = args.<String>getOne("data").get();
                    PROPERTY_DETAIL.accept(src, data, player);
                    return CommandResult.success();
                })
                .build();

        Function<WeaponClass, List<Text>> TO_TEXT = weaponClass -> {
            List<Text> list = new ArrayList<>();
            PropertyService ps = IoC.get().build(PropertyService.class);
            list.add(Text.of(TextColors.GOLD, weaponClass.getName()));
            for (Integer property : weaponClass.getProperties()) {
                list.add(Text.of(TextColors.GRAY, " -> ", ps.getNameById(property)));
            }
            for (Integer property : weaponClass.getPropertiesMults()) {
                list.add(Text.of(TextColors.GRAY, " -> ", ps.getNameById(property)));
            }
            return list;
        };

        CommandSpec inspectItemDamage = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player")))
                )
                .executor((src, args) -> {
                    Player player = args.<Player>getOne("player").get();
                    ItemService is = IoC.get().build(ItemService.class);
                    Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
                    if (!itemInHand.isPresent()) {
                        src.sendMessage(Text.of(player.getName() + " has no item in main hand"));
                        return CommandResult.empty();
                    }
                    ItemStack itemStack = itemInHand.get();
                    RPGItemType fromItemStack = is.getFromItemStack(itemStack);
                    WeaponClass weaponClass = fromItemStack.getWeaponClass();
                    List<WeaponClass> parents = new LinkedList<>();
                    WeaponClass parent = weaponClass.getParent();
                    List<Integer> o = new ArrayList<>();
                    o.addAll(weaponClass.getProperties());
                    o.addAll(weaponClass.getPropertiesMults());
                    while (parent != null) {
                        parents.add(parent);
                        o.addAll(parent.getPropertiesMults());
                        o.addAll(parent.getProperties());
                        parent = parent.getParent();
                    }
                    parents.add(weaponClass);
                    Collections.reverse(parents);

                    List<Text> a = new ArrayList<>();
                    for (WeaponClass wc : parents) {
                        a.addAll(TO_TEXT.apply(wc));
                    }
                    for (Text text : a) {
                        src.sendMessage(text);
                    }
                    src.sendMessage(Text.of(TextColors.GOLD, "=================="));
                    DamageService ds = IoC.get().build(DamageService.class);
                    CharacterService cs = IoC.get().build(CharacterService.class);
                    PropertyService ps = IoC.get().build(PropertyService.class);
                    IActiveCharacter character = cs.getCharacter(player);
                    src.sendMessage(Text.of(TextColors.RED, "Damage: ", ds.getCharacterItemDamage(character, fromItemStack)));
                    src.sendMessage(Text.of(TextColors.RED, "Details: "));
                    src.sendMessage(Text.of(TextColors.GRAY, " - From Item: ", character.getBaseWeaponDamage(fromItemStack)));

                    Collection<PlayerClassData> values = character.getClasses().values();
                    for (PlayerClassData value : values) {
                        Set<ConfigRPGItemType> configRPGItemTypes = value.getClassDefinition().getWeapons().get(itemInHand.get());
                        if (configRPGItemTypes != null) {
                            for (ConfigRPGItemType w : configRPGItemTypes) {
                                if (w.rpgItemType.equals(fromItemStack)) {
                                    src.sendMessage(Text.of(TextColors.GRAY, "  - From Class: " + w.damage));
                                }
                            }
                        }
                    }


                    src.sendMessage(Text.of(TextColors.GRAY, " - From WeaponClass: "));
                    Iterator<Integer> iterator = o.iterator();
                    while (iterator.hasNext()) {
                        int integer = iterator.next();
                        String nameById = ps.getNameById(integer);
                        if (!nameById.endsWith("_mult")) {
                            iterator.remove();
                        } else continue;
                        src.sendMessage(Text.of(TextColors.GRAY, "   - ", nameById, ":", cs.getCharacterProperty(character, integer)));
                    }
                    src.sendMessage(Text.of(TextColors.GRAY, "   - Mult: "));
                    iterator = o.iterator();
                    while (iterator.hasNext()) {
                        int integer = iterator.next();
                        String nameById = ps.getNameById(integer);
                        src.sendMessage(Text.of(TextColors.GRAY, "   - ", nameById, ":", cs.getCharacterProperty(character, integer)));
                    }


                    return CommandResult.success();
                })
                .build();


        CommandSpec invoke = CommandSpec.builder()
                .description(Text.builder("Command which let you execute commands as another player.")
                        .append(Text.builder("/nadmin invoke SomePlayer classes Primary").color(TextColors.LIGHT_PURPLE).build())
                        .append(Text.builder("will class selection GUI on the client side of the player SomePlayer. Does not bypass any permissions.").build()).build())
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.remainingJoinedStrings(Text.of("command"))
                )
                .executor((src, args) -> {
                    args.<Player>getOne(Text.of("player")).ifPresent(p -> {
                        args.<String>getOne(Text.of("command")).ifPresent(c -> {
                            Sponge.getCommandManager().process(p, c);
                        });
                    });
                    return CommandResult.success();
                })
                .build();


        CommandSpec inspect = CommandSpec.builder()
                .child(inspectProperty, "property", "p")
                .child(inspectItemDamage, "itemDamage", "idmg")
                .build();

        CommandSpec adminRoot = CommandSpec
                .builder()
                .description(TextSerializers
                        .FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_ADMIN_DESC))
                .permission("ntrpg.admin")
                .child(executeSkill, "skill", "s")
                .child(enchant, "enchant", "e")
                .child(socket, "socket", "sk")
                .child(rune, "rune", "r")
                .child(runeword, "runeword", "rw")
                .child(exp, "experiences", "exp")
                .child(effect, "effect", "ef")
                .child(reload, "reload")
                .child(rarity, "rarity", "rrty")
                .child(meta, "itemmeta", "imeta", "imt")
                .child(rst, "grouprequirements", "gr")
                .child(mt, "itemType", "it", "type")
                .child(inspect, "i", "inspect")
                .child(invoke, "invoke", "invk")
                .build();

        Sponge.getCommandManager().register(this, adminRoot, "nadmin", "na");
    }


    public void registerCharacterCommands() {


        // ===========================================================
        // ==============           CHAR CREATE         ==============
        // ===========================================================

        CommandSpec createCharacter = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_CREATE_DESCRIPTION))
                .arguments(GenericArguments.remainingJoinedStrings(TextHelper.parse("name")))
                .permission("ntrpg.player.character.create")
                .executor((src, args) -> {
                    String a = args.<String>getOne("name").get();
                    CompletableFuture.runAsync(() -> {
                        Player player = (Player) src;
                        CharacterService characterService = IoC.get().build(CharacterService.class);
                        int i = characterService.canCreateNewCharacter(player.getUniqueId(), a);
                        if (i == 1) {
                            src.sendMessage(Localizations.REACHED_CHARACTER_LIMIT.toText());
                        } else if (i == 2) {
                            src.sendMessage(Localizations.CHARACTER_EXISTS.toText());
                        } else if (i == 0) {
                            CharacterBase characterBase = new CharacterBase();
                            characterBase.setUuid(player.getUniqueId());
                            characterBase.setName(a);
                            characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);
                            characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);

                            characterService.createAndUpdate(characterBase);

                            src.sendMessage(TextHelper.parse(CommandLocalization.CHARACTER_CREATED.replaceAll("%1", characterBase.getName())));
                            Gui.sendListOfCharacters(characterService.getCharacter(player.getUniqueId()), characterBase);
                        }
                    }, asyncExecutor);
                    return CommandResult.success();
                })
                .build();

        // ===========================================================
        // ==============           CHAR CREATE         ==============
        // ===========================================================
        CommandSpec deleteCharacter = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_DELETE_DESCRIPTION))
                .arguments(GenericArguments.remainingJoinedStrings(TextHelper.parse("name")))
                .permission("ntrpg.player.character.delete")
                .executor((src, args) -> {
                    String a = args.<String>getOne("name").get();
                    Player player = (Player) src;
                    CharacterService characterService = IoC.get().build(CharacterService.class);
                    IActiveCharacter character = characterService.getCharacter(player);
                    if (character.getName().equalsIgnoreCase(a)) {
                        characterService.removeCachedCharacter(player.getUniqueId());
                        characterService.registerDummyChar(characterService.buildDummyChar(player.getUniqueId()));
                    }
                    CompletableFuture.runAsync(() -> {
                        characterService.markCharacterForRemoval(player.getUniqueId(), a);
                        player.sendMessage(Localizations.CHAR_DELETED_FEEDBACK.toText());
                    }, asyncExecutor);
                    return CommandResult.success();
                })
                .build();


        CommandSpec setclass = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
                .arguments(new AnyClassDefCommandElement(TextHelper.parse("class")))
                .permission("ntrpg.player.set.class")
                .executor((src, args) -> {
                    ClassDefinition configClass = args.<ClassDefinition>getOne("class").get();

                    if (!src.hasPermission("ntrpg.class." + configClass.getName().toLowerCase())) {
                        src.sendMessage(Localizations.NO_PERMISSIONS.toText());
                        return CommandResult.empty();
                    }
                    if (!(src instanceof Player)) {
                        throw new IllegalStateException("Cannot be run as a console");
                    }
                    Player player = (Player) src;
                    IActiveCharacter character = GlobalScope.characterService.getCharacter(player.getUniqueId());
                    if (character.isStub()) {
                        player.sendMessage(Localizations.CHARACTER_IS_REQUIRED.toText());
                        return CommandResult.empty();
                    }
                    GlobalScope.characterService.addNewClass(character, configClass);
                    return CommandResult.success();
                })
                .build();


        CommandSpec cset = CommandSpec.builder()
                .child(setclass, "class", "c")
                .build();

        CommandSpec learn = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_SKILL_LEARN))
                .arguments(
                        new UnlearnedSkillCommandElement(Text.of("skill")),
                        new PlayerClassCommandElement(Text.of("class"))
                )
                .permission("ntrpg.player.skills")
                .executor((src, args) -> {
                    args.<ClassDefinition>getOne(Text.of("class")).ifPresent(aClass -> {
                        args.<ISkill>getOne(Text.of("skill")).ifPresent(iSkill -> {
                            Player player = (Player) src;
                            if (aClass.getSkillTree() != null) {
                                IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
                                PlayerClassData playerClassData = character.getClasses().get(aClass.getName());
                                aClass.getSkillTreeType().processLearnSkill(character, playerClassData, iSkill);
                            } else {
                                player.sendMessage(Localizations.CLASS_HAS_NO_SKILLTREE.toText(Arg.arg("class", aClass.getName())));
                            }
                        });
                    });
                    return CommandResult.empty();
                })
                .build();

        CommandSpec upgrade = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_SKILL_UPGRADE))
                .arguments(
                        new UnlearnedSkillCommandElement(Text.of("skill")),
                        new PlayerClassCommandElement(Text.of("class"))
                )
                .permission("ntrpg.player.skills")
                .executor((src, args) -> {
                    args.<ClassDefinition>getOne(Text.of("class")).ifPresent(aClass -> {
                        args.<ISkill>getOne("skill").ifPresent(iSkill -> {
                            Player player = (Player) src;
                            if (aClass.getSkillTree() != null) {
                                IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
                                PlayerClassData playerClassData = character.getClasses().get(aClass.getName());
                                aClass.getSkillTreeType().processUpgradeSkill(character, playerClassData, iSkill);
                            } else {
                                player.sendMessage(Localizations.CLASS_HAS_NO_SKILLTREE.toText(Arg.arg("class", aClass.getName())));
                            }
                        });
                    });
                    return CommandResult.success();
                })
                .build();

        CommandSpec refund = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_SKILL_REFUND))
                .arguments(
                        new UnlearnedSkillCommandElement(Text.of("skill")),
                        new PlayerClassCommandElement(Text.of("class"))
                )
                .permission("ntrpg.player.skills.refund")
                .executor((src, args) -> {
                    args.<ClassDefinition>getOne(Text.of("class")).ifPresent(aClass -> {
                        args.<ISkill>getOne("skill").ifPresent(iSkill -> {
                            Player player = (Player) src;
                            if (aClass.getSkillTree() != null) {
                                IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
                                PlayerClassData playerClassData = character.getClasses().get(aClass.getName());
                                aClass.getSkillTreeType().processRefundSkill(character, playerClassData, iSkill);
                            } else {
                                player.sendMessage(Localizations.CLASS_HAS_NO_SKILLTREE.toText(Arg.arg("class", aClass.getName())));
                            }
                        });
                    });
                    return CommandResult.success();
                })
                .build();

        CommandSpec cskill = CommandSpec.builder()
                .child(learn, "learn", "l")
                .child(upgrade, "upgrade", "u")
                .child(refund, "refund", "r")
                .build();

        CommandSpec cattribute = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_ATTRIBUTE))
                .arguments(new CharacterAttributeCommandElement(Text.of("attribute")),
                        GenericArguments.integer(Text.of("amount")))
                .executor((src, args) -> {
                    args.<ICharacterAttribute>getOne(Text.of("attribute")).ifPresent(iCharacterAttribute -> {
                        Integer i = args.<Integer>getOne("amount").orElse(1);
                        IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
                        GlobalScope.characterService.addAttribute(character, iCharacterAttribute, i);
                        GlobalScope.characterService.putInSaveQueue(character.getCharacterBase());
                    });
                    return CommandResult.empty();
                })
                .build();

        CommandSpec cswitch = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                .executor((src, args) -> {
                    args.<String>getOne("name").ifPresent(s -> {
                        Player player = (Player) src;
                        IActiveCharacter current = GlobalScope.characterService.getCharacter(player);
                        if (current != null && current.getName().equalsIgnoreCase(s)) {
                            player.sendMessage(Localizations.ALREADY_CUURENT_CHARACTER.toText());
                            return;
                        }
                        CompletableFuture.runAsync(() -> {
                            List<CharacterBase> playersCharacters = GlobalScope.characterService.getPlayersCharacters(player.getUniqueId());
                            boolean b = false;
                            for (CharacterBase playersCharacter : playersCharacters) {
                                if (playersCharacter.getName().equalsIgnoreCase(s)) {
                                    ActiveCharacter character =
                                            GlobalScope.characterService.createActiveCharacter(player.getUniqueId(), playersCharacter);
                                    Sponge.getScheduler().createTaskBuilder().name("SetCharacterCallback" + player.getUniqueId())
                                            .execute(() -> {
                                                GlobalScope.characterService.setActiveCharacter(player.getUniqueId(), character);
                                                GlobalScope.characterService.invalidateCaches(character);
                                                GlobalScope.characterService.assignPlayerToCharacter(player);
                                            })
                                            .submit(NtRpgPlugin.this);
                                    b = true;
                                    //Update characterbase#updated, so next time plazer logs it it will autoselect this character,
                                    // even if it was never updated afterwards
                                    GlobalScope.characterService.save(playersCharacter);
                                    break;
                                }
                            }
                            if (!b) {
                                player.sendMessage(Localizations.NON_EXISTING_CHARACTER.toText());
                            }
                        }, asyncExecutor);
                    });
                    return CommandResult.success();
                })
                .build();

        CommandSpec cslist = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CHARACTE_LIST))
                .executor((src, args) -> {
                    IActiveCharacter character = GlobalScope.characterService.getCharacter(((Player) src).getUniqueId());
                    Gui.sendListOfCharacters(character, character.getCharacterBase());
                    return CommandResult.success();
                })
                .build();

        CommandSpec characterRoot = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
                .child(createCharacter, "create", "c")
                .child(cset, "set", "s")
                .child(cskill, "skill", "s", "sk")
                .child(cattribute, "attribute", "attr", "a")
                .child(cswitch, "switch")
                .child(cslist, "list")
                .child(deleteCharacter, "remove", "rm")
                .build();

        Sponge.getCommandManager().register(this, characterRoot, "character", "char", "nc");

        // ===========================================================
        // =================          SKILLS         =================
        // ===========================================================

        CommandSpec skillexecute = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_HP_DESC))
                .arguments(new LearnedSkillCommandElement(TextHelper.parse("skill")))
                .executor((src, args) -> {
                    IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
                    args.<ISkill>getOne(Text.of("skill")).ifPresent(iSkill -> {
                        PlayerSkillContext info = character.getSkillInfo(iSkill.getId());
                        if (info == PlayerSkillContext.Empty || info == null) {
                            src.sendMessage(Localizations.CHARACTER_DOES_NOT_HAVE_SKILL.toText(Arg.arg("skill", iSkill.getName())));
                        }
                        GlobalScope.skillService.executeSkill(character, info, new SkillExecutorCallback() {
                            @Override
                            public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                                switch (skillResult.getResult()) {
                                    case ON_COOLDOWN:
                                        break;
                                    case NO_MANA:
                                        Gui.sendMessage(character, Localizations.NO_MANA, Arg.EMPTY);
                                        break;
                                    case NO_HP:
                                        Gui.sendMessage(character, Localizations.NO_HP, Arg.EMPTY);
                                        break;
                                    case CASTER_SILENCED:
                                        Gui.sendMessage(character, Localizations.PLAYER_IS_SILENCED, Arg.EMPTY);
                                        break;
                                    case NO_TARGET:
                                        Gui.sendMessage(character, Localizations.NO_TARGET, Arg.EMPTY);
                                }
                            }
                        });
                    });
                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(this, skillexecute, "skill", "skl", "ns");

        // ===========================================================
        // ==============              MP HP            ==============
        // ===========================================================


        CommandSpec hp = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_HP_DESC))
                .executor((src, args) -> {

                    return CommandResult.success();
                })
                .build();

        CommandSpec mp = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_MP_DESC))
                .executor((src, args) -> {
                    final Player player = (Player) src;
                    IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
                    if (character.isStub()) {
                        character.getPlayer().sendMessage(Localizations.NO_CHARACTER.toText());
                        return CommandResult.empty();
                    }
                    Gui.displayMana(character);
                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(this, hp, "health");
        Sponge.getCommandManager().register(this, mp, "mana", "mp");

        // ===========================================================
        // =================          PARTY          =================
        // ===========================================================
        CommandSpec createparty = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_BIND_DESC))
                .permission("ntrpg.player.party.create")
                .executor((src, args) -> {
                    IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
                    if (character.isStub()) {
                        Gui.sendMessage(character, Localizations.CHARACTER_IS_REQUIRED, Arg.EMPTY);
                        return CommandResult.success();
                    }
                    if (character.hasParty()) {
                        Gui.sendMessage(character, Localizations.ALREADY_IN_PARTY, Arg.EMPTY);
                        return CommandResult.success();
                    }
                    Party party = new Party(character);
                    character.setParty(party);
                    Gui.sendMessage(character, Localizations.PARTY_CREATED, Arg.EMPTY);
                    return CommandResult.success();
                })
                .build();

        CommandSpec kick = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_BIND_DESC))
                .permission("ntrpg.player.party.create")
                .arguments(new PartyMemberCommandElement(TextHelper.parse("player")))
                .executor((src, args) -> {
                    args.<IActiveCharacter>getOne(TextHelper.parse("player")).ifPresent(o -> {

                        GlobalScope.partyService.kickCharacterFromParty(
                                GlobalScope.characterService.getCharacter((Player) src).getParty(), o);
                    });
                    return CommandResult.success();
                })
                .build();


        CommandSpec invite = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_BIND_DESC))
                .permission("ntrpg.player.party.create")
                .arguments(GenericArguments.player(TextHelper.parse("player")))
                .executor((src, args) -> {
                    args.<Player>getOne(TextHelper.parse("player")).ifPresent(o -> {
                        GlobalScope.partyService.sendPartyInvite(
                                GlobalScope.characterService.getCharacter((Player) src).getParty(),
                                GlobalScope.characterService.getCharacter(o));
                    });
                    return CommandResult.success();
                })
                .build();


        CommandSpec accept = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_BIND_DESC))
                .permission("ntrpg.player.party.create")
                .executor((src, args) -> {
                    IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
                    if (character.getPendingPartyInvite() != null) {
                        GlobalScope.partyService.addToParty(character.getPendingPartyInvite(), character);
                    }
                    return CommandResult.success();
                })
                .build();


        CommandSpec partyRoot = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
                .child(createparty, "create", "c")
                .child(kick, "kick", "k")
                .child(invite, "invite", "i")
                .child(accept, "accept", "a")
                .build();


        Sponge.getCommandManager().register(this, partyRoot, "party", "np", "nparty");

        // ===========================================================
        // =================          Groups         =================
        // ===========================================================

        CommandSpec classes = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_CLASSES_DESC))
                .arguments(new ClassTypeCommandElement(Text.of("type")))
                .permission("ntrpg.classes.list")
                .executor((src, args) -> {
                    args.<String>getOne(Text.of("type")).ifPresent(o -> {
                        IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
                        Gui.filterClassesByType(character, o);
                    });
                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(this, classes, "classes");

        CommandSpec classgui = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_RACE_DESC))
                .arguments(new AnyClassDefCommandElement(Text.of("class"), false))
                .executor((src, args) -> {
                    args.<ClassDefinition>getOne(Text.of("class")).ifPresent(o -> {
                        IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
                        Gui.showClassInfo(character, o);
                    });
                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(this, classgui, "class");


        CommandSpec weapon = CommandSpec.builder()
                .arguments(new ClassDefCommandElement(Text.of("class")))
                .executor((src, args) -> {
                    args.<ClassDefinition>getOne(Text.of("class"))
                            .ifPresent(playerGroup -> {
                                Player player = (Player) src;
                                Gui.displayClassWeapons(playerGroup, player);
                            });
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(this, weapon, "weapons", "wp");

        CommandSpec armor = CommandSpec.builder()
                .arguments(new ClassDefCommandElement(Text.of("class")))
                .executor((src, args) -> {

                    args.<ClassDefinition>getOne(Text.of("class"))
                            .ifPresent(playerGroup -> {
                                Player player = (Player) src;
                                Gui.displayClassArmor(playerGroup, player);
                            });
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(this, armor, "armor");


        CommandSpec runes = CommandSpec.builder()
                .permission("ntrpg.runes.list")
                .executor((src, args) -> {
                    Gui.sendListOfRunes(GlobalScope.characterService.getCharacter((Player) src));
                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(this, runes, "runes");
    }

    public void registerSkillCommands() {
        // ===========================================================
        // ===============          SKILL BIND         ===============
        // ===========================================================
        CommandSpec bind = CommandSpec.builder()
                .description(TextSerializers.FORMATTING_CODE
                        .deserialize(CommandLocalization.COMMAND_BIND_DESC))
                .permission("ntrpg.player.skillbind")
                .arguments(
                        new LearnedSkillCommandElement(TextHelper.parse("skill"))
                )
                .executor((src, args) -> {
                    Optional<ISkill> skill = args.getOne("skill");
                    if (skill.isPresent()) {
                        ISkill iSkill = skill.get();
                        if (!(iSkill instanceof ActiveSkill)) {

                            return CommandResult.empty();
                        }
                        Player pl = (Player) src;
                        IActiveCharacter character = GlobalScope.characterService.getCharacter(pl);
                        if (character.isStub()) {
                            return CommandResult.empty();
                        }
                        ItemStack is = NtRpgPlugin.GlobalScope.inventorySerivce.createSkillbind(iSkill);
                        pl.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class)).offer(is);
                    }

                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(this, bind, "bind", "nb");
    }
}
