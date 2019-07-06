package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.commands.InfoCommand;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.inventory.data.InventoryCommandItemMenuData;
import cz.neumimto.rpg.sponge.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.sponge.inventory.data.SkillTreeInventoryViewControllsData;
import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;
import cz.neumimto.rpg.sponge.listeners.SkillTreeInventoryListener;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.AcceptsItems;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.sponge.gui.CatalogTypeItemStackBuilder.Block;
import static cz.neumimto.rpg.sponge.gui.CatalogTypeItemStackBuilder.Item;

/**
 * Created by ja on 29.12.2016.
 */
public class GuiHelper {

    public static Map<DamageType, CatalogTypeItemStackBuilder> damageTypeToItemStack = new HashMap<>();
    private static NtRpgPlugin plugin;

    static {
        plugin = NtRpgPlugin.GlobalScope.plugin;

        damageTypeToItemStack.put(DamageTypes.ATTACK, Item.of(ItemTypes.STONE_SWORD));
        damageTypeToItemStack.put(DamageTypes.CONTACT, Item.of(ItemTypes.CACTUS));

        damageTypeToItemStack.put(DamageTypes.CUSTOM, Item.of(ItemTypes.BARRIER));

        damageTypeToItemStack.put(DamageTypes.DROWN, Block.of(BlockTypes.WATER));
        damageTypeToItemStack.put(DamageTypes.EXPLOSIVE, Item.of(ItemTypes.TNT));
        damageTypeToItemStack.put(DamageTypes.FALL, Item.of(ItemTypes.IRON_BOOTS));
        damageTypeToItemStack.put(DamageTypes.FIRE, Item.of(ItemTypes.BLAZE_POWDER));

        damageTypeToItemStack.put(DamageTypes.HUNGER, Item.of(ItemTypes.ROTTEN_FLESH));
        damageTypeToItemStack.put(DamageTypes.MAGMA, Block.of(BlockTypes.LAVA));
        damageTypeToItemStack.put(DamageTypes.PROJECTILE, Item.of(ItemTypes.TIPPED_ARROW));
        damageTypeToItemStack.put(DamageTypes.VOID, Block.of(BlockTypes.PORTAL));
        damageTypeToItemStack.put(DamageTypes.MAGIC, Item.of(ItemTypes.ENCHANTED_BOOK));

        damageTypeToItemStack.put(NDamageType.FIRE, Block.of(BlockTypes.FIRE));
        damageTypeToItemStack.put(NDamageType.ICE, Block.of(BlockTypes.ICE));
        damageTypeToItemStack.put(NDamageType.LIGHTNING, Item.of(ItemTypes.NETHER_STAR));
    }

    public static ItemStack itemStack(ItemType type) {
        ItemStack is = ItemStack.of(type, 1);
        is.offer(new MenuInventoryData(true));
        is.offer(Keys.HIDE_ATTRIBUTES, true);
        is.offer(Keys.HIDE_MISCELLANEOUS, true);
        return is;
    }

    private static ItemStack damageTypeToItemStack(String type) {
        if (type == null) {
            return itemStack(ItemTypes.STONE);
        }
        CatalogTypeItemStackBuilder a = damageTypeToItemStack.get(type);
        ItemStack is = null;
        if (a == null) {
            is = itemStack(ItemTypes.STONE);
        } else {
            is = a.toItemStack();
        }
        is.offer(Keys.DISPLAY_NAME, Text.of(type));
        return is;
    }

    static Inventory createMenuInventoryClassDefView(ClassDefinition w) {
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.of(w.getName(), toTextColor(w.getPreferedColor()), TextStyles.BOLD)))
                .build(plugin);
        String dyeColor = NtRpgPlugin.pluginConfig.CLASS_TYPES.get(w.getClassType()).getDyeColor();
        makeBorder(i, toDyeColor(dyeColor));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 4))).offer(toItemStack(w));

        if (!w.getWeapons().isEmpty()) {
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 2))).offer(createWeaponCommand(w));
        }
        if (!w.getAllowedArmor().isEmpty()) {
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 2))).offer(createArmorCommand(w));
        }
        if (!w.getStartingAttributes().isEmpty()) {
            i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 3))).offer(createAttributesCommand(w));
        }

        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 3))).offer(createPropertyCommand(w));

        return i;
    }

    static Inventory createMenuInventoryClassTypeView(String type) {
        ClassTypeDefinition classTypeDefinition = NtRpgPlugin.pluginConfig.CLASS_TYPES.get(type);
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(
                        Text.builder("[ ").color(toTextColor(classTypeDefinition.getSecondaryColor()))
                                .append(Text.builder(type).color(toTextColor(classTypeDefinition.getPrimaryColor())).style(TextStyles.BOLD).build())
                                .append(Text.builder(" ]").color(toTextColor(classTypeDefinition.getSecondaryColor())).build())
                                .build()))
                .build(plugin);
        GuiHelper.makeBorder(i, toDyeColor(classTypeDefinition.getDyeColor()));

        NtRpgPlugin.GlobalScope.classService.getClassDefinitions().stream()
                .filter(a -> a.getClassType().equalsIgnoreCase(type))
                .forEach(a -> i.offer(GuiHelper.toItemStack(a)));

        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 4))).offer(back("classes", translate(LocalizationKeys.BACK)));

        return i;
    }

    public static Inventory createMenuInventoryClassTypesView() {
        Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(translate(LocalizationKeys.CLASS_TYPES)))
                .build(plugin);

        makeBorder(i, DyeColors.WHITE);

        for (String type : NtRpgPlugin.pluginConfig.CLASS_TYPES.keySet()) {
            i.offer(createClassTypeDefinitionCommand(type));
        }

        return i;
    }

    public static ItemStack createClassTypeDefinitionCommand(String type) {
        ItemStack i = itemStack(ItemTypes.CRAFTING_TABLE);
        i.offer(new MenuInventoryData(true));
        i.offer(Keys.DISPLAY_NAME,
                Text.builder(type)
                        .color(toTextColor(Rpg.get().getPluginConfig().CLASS_TYPES.get(type).getPrimaryColor()))
                        .build());
        i.offer(new InventoryCommandItemMenuData("classes " + type));
        return i;
    }

    private static ItemStack createPropertyCommand(ClassDefinition group) {
        String cc = NtRpgPlugin.GlobalScope.injector.getInstance(InfoCommand.class).getAliases().iterator().next();
        return command(cc + " properties-initial " + group.getName(),
                translate(LocalizationKeys.PROPERTIES), ItemTypes.BOOK);
    }

    private static ItemStack createAttributesCommand(ClassDefinition group) {
        String cc = NtRpgPlugin.GlobalScope.injector.getInstance(InfoCommand.class).getAliases().iterator().next();
        return command(cc + "  attributes-initial " + group.getName(),
                translate(LocalizationKeys.ATTRIBUTES), ItemTypes.BOOK);
    }

    private static ItemStack createArmorCommand(ClassDefinition group) {
        ItemStack i = command("armor " + group.getName(),translate(LocalizationKeys.ARMOR), ItemTypes.DIAMOND_CHESTPLATE);
        i.offer(Keys.ITEM_LORE, Collections.singletonList(translate(LocalizationKeys.ARMOR_MENU_HELP)));
        return i;
    }

    private static ItemStack createWeaponCommand(ClassDefinition group) {
        ItemStack i = command("weapons " + group.getName(),translate(LocalizationKeys.WEAPONS), ItemTypes.DIAMOND_SWORD);
        i.offer(Keys.ITEM_LORE, Collections.singletonList(translate(LocalizationKeys.WEAPONS_MENU_HELP)));
        return i;
    }

    public static ItemStack propertyToItemStack(int id, float value) {
        ItemStack i = itemStack(ItemTypes.BOOK);
        String nameById = NtRpgPlugin.GlobalScope.spongePropertyService.getNameById(id);
        nameById = Utils.configNodeToReadableString(nameById);
        i.offer(Keys.DISPLAY_NAME, TextHelper.makeText(nameById, TextColors.GREEN));
        if (nameById.endsWith("mult")) {
            i.offer(Keys.ITEM_LORE, Collections.singletonList(TextHelper.makeText((value * 100) + "%", TextColors.GOLD)));
        } else {
            i.offer(Keys.ITEM_LORE, Collections.singletonList(TextHelper.makeText(String.valueOf(value), TextColors.GOLD)));
        }
        i.offer(new MenuInventoryData(true));
        return i;
    }

    static List<Text> getItemLore(String s) {
        String[] a = s.split("\\n");
        List<Text> t = new ArrayList<>();
        for (String s1 : a) {
            t.add(Text.builder(s1).color(TextColors.GOLD).style(TextStyles.ITALIC).build());
        }
        return t;
    }

    public static ItemStack back(String command, Text displayName) {
        return command(command, displayName, ItemTypes.PAPER);
    }

    public static ItemStack command(String command, Text displayName, ItemType type) {
        ItemStack of = itemStack(type);
        of.offer(Keys.DISPLAY_NAME, displayName);
        of.offer(new MenuInventoryData(true));
        of.offer(new InventoryCommandItemMenuData(command));
        return of;
    }

    public static ItemStack back(ClassDefinition g) {
        return command("class " + g.getName(), translate(LocalizationKeys.BACK), ItemTypes.PAPER);
    }

    public static ItemStack unclickableInterface(DyeColor dyeColor) {
        ItemStack of = itemStack(ItemTypes.STAINED_GLASS_PANE);
        of.offer(new MenuInventoryData(true));
        of.offer(Keys.DYE_COLOR, dyeColor);
        of.offer(Keys.DISPLAY_NAME, Text.EMPTY);
        return of;
    }

    static ItemStack skillToItemStack(ISpongeCharacter character, SkillData skillData, SkillTree skillTree, SkillTreeViewModel model) {
        return toItemStack(skillData.getSkill(), character, skillData, skillTree, model);
    }

    private static ItemStack toItemStack(ISkill skill, ISpongeCharacter character, SkillData skillData, SkillTree skillTree, SkillTreeViewModel model) {
        List<Text> lore;
        TextColor nameColor;
        Pair<List<Text>, TextColor> fromCache = model.getFromCache(skill);
        ItemStack itemStack = itemStack(NtRpgPlugin.GlobalScope.inventorySerivce.getItemIconForSkill(skill));
        if (fromCache == null) {
            lore = new ArrayList<>();
            nameColor = getSkillTextColor(character, skill, skillData, skillTree);
            if (skillData.useDescriptionOnly()) {
                List<String> description = skillData.getDescription(character);
                for (String s : description) {
                    lore.add(TextHelper.parse(s));
                }
            } else {
                LocalizationService locService = Rpg.get().getLocalizationService();
                Text execType = TextHelper.parse(locService.translate(skill.getSkillExecutionType().toString().toLowerCase()));
                lore.add(execType);
                lore.add(Text.EMPTY);

                for (String s : skillData.getDescription(character)) {
                    lore.add(TextHelper.parse(s));
                }

                lore.add(Text.EMPTY);

                Set<ISkillType> skillTypes = skillData.getSkill().getSkillTypes();
                Text.Builder builder = Text.builder();
                Iterator<ISkillType> iterator = skillTypes.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    i++;
                    ISkillType next = iterator.next();
                    String translate = locService.translate(next.toString());
                    lore.add(TextHelper.parse(translate));
                    if (i % 3 == 0) {
                        lore.add(builder.build());
                        builder = Text.builder();
                    }
                }
            }

            model.addToCache(skill, lore, nameColor);
        } else {
            lore = fromCache.key;
            nameColor = fromCache.value;
        }
        itemStack.offer(Keys.DISPLAY_NAME, Text.builder(skillData.getSkillName()).style(TextStyles.BOLD).color(nameColor).build());
        itemStack.offer(Keys.ITEM_LORE, lore);
        return itemStack;
    }

    @SuppressWarnings("unchecked")
    private static TextColor getSkillTextColor(IActiveCharacter character, ISkill skill, SkillData skillData, SkillTree skillTree) {
        if (character.hasSkill(skillData.getSkillId())) {
            return TextColors.GREEN;
        }
        Collection<PlayerClassData> values = character.getClasses().values();
        Optional<PlayerClassData> first = values.stream().filter(a -> a.getClassDefinition().getSkillTree() == skillTree).findFirst();
        return first.filter(playerClassData -> Rpg.get().getCharacterService().canLearnSkill(character, playerClassData.getClassDefinition(), skill).isOk()).map(playerClassData -> TextColors.GRAY).orElse(TextColors.RED);
    }


    public static Inventory createSkillTreeInventoryViewTemplate(ISpongeCharacter character, SkillTree skillTree) {
        Inventory i = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(translate(LocalizationKeys.SKILLTREE)))
                .property(AcceptsItems.of(Collections.EMPTY_LIST))
                .listener(ClickInventoryEvent.Primary.class,
                        event -> new SkillTreeInventoryListener().onOptionSelect(event, (Player) event.getCause().root()))
                .listener(ClickInventoryEvent.Secondary.class,
                        event -> new SkillTreeInventoryListener().onOptionSelect(event, (Player) event.getCause().root()))
                .build(plugin);

        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 0))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 1))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 2))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 3))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 4))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 5))).offer(unclickableInterface(DyeColors.YELLOW));


        SkillTreeViewModel model = character.getSkillTreeViewLocation().get(skillTree.getId());

        ItemStack md = interactiveModeToitemStack(character, model.getInteractiveMode());
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 1))).set(md);

        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 2))).offer(createControlls(SkillTreeControllsButton.NORTH));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 3))).offer(createControlls(SkillTreeControllsButton.SOUTH));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 4))).offer(createControlls(SkillTreeControllsButton.WEST));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 5))).offer(createControlls(SkillTreeControllsButton.EAST));

        return i;
    }

    public static ItemStack createControlls(SkillTreeControllsButton button) {
        ItemStack itemStack = VanillaMessaging.controlls.get(button).toItemStack();
        itemStack.offer(new SkillTreeInventoryViewControllsData(button));
        itemStack.offer(new MenuInventoryData(true));
        return itemStack;
    }

    public static ItemStack createSkillTreeInventoryMenuBoundary() {
        ItemStack of = itemStack(ItemTypes.STAINED_GLASS_PANE);
        of.offer(Keys.DISPLAY_NAME, Text.EMPTY);
        of.offer(new MenuInventoryData(true));
        of.offer(Keys.DYE_COLOR, DyeColors.RED);
        return of;
    }

    public static ItemStack createSkillTreeConfirmButtom() {
        ItemStack itemStack = itemStack(ItemTypes.KNOWLEDGE_BOOK);
        itemStack.offer(Keys.DISPLAY_NAME, translate(LocalizationKeys.CONFIRM_SKILL_SELECTION_BUTTON));
        itemStack.offer(new SkillTreeInventoryViewControllsData(SkillTreeControllsButton.CONFIRM));
        return itemStack;
    }

    public static Inventory createSkillDetailInventoryView(ISpongeCharacter character, SkillTree skillTree, SkillData skillData) {
        Inventory build = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.builder(skillData.getSkill().getLocalizableName()).color(TextColors.DARK_GREEN).style(TextStyles.BOLD)
                        .build()))
                .build(plugin);

        SkillTreeViewModel skillTreeViewModel = character.getLastTimeInvokedSkillTreeView();
        ItemStack back = back("skilltree " + skillTreeViewModel.getViewedClass().getName(), translate(LocalizationKeys.SKILLTREE));
        build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back);

        if (skillData instanceof SkillPathData) {
            SkillPathData data = (SkillPathData) skillData;

            ItemStack of = itemStack(ItemTypes.PAPER);
            of.offer(Keys.DISPLAY_NAME, Text.of("Tier " + data.getTier()));
            of.offer(new MenuInventoryData(true));
            build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 0))).offer(of);

            SkillService skillService = NtRpgPlugin.GlobalScope.skillService;

            int i = 0;
            int j = 2;
            for (Map.Entry<String, Integer> entry : data.getSkillBonus().entrySet()) {
                ISkill skill = skillService.getById(entry.getKey()).orElse(null);
                if (skill != null) {
                    ItemStack itemStack = skillToItemStack(character, character.getSkill(skill.getId()).getSkillData(), skillTree, skillTreeViewModel);
                    itemStack.offer(Keys.DISPLAY_NAME, Text
                            .builder(String.format("%+d", entry.getValue()) + " | " + entry.getKey())
                            .color(entry.getValue() < 0 ? TextColors.RED : TextColors.DARK_GREEN)
                            .build());
                    build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(j, i))).offer(itemStack);
                    if (j > 8) {
                        j = 0;
                        i++;
                    } else {
                        j++;
                    }
                }
            }

        } else {
            String type = skillData.getSkill().getDamageType();
            if (type != null) {
                build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 1))).offer(damageTypeToItemStack(type));
            }

            List<ItemStack> itemStacks = configurationToItemStacks(skillData);
            int m, n, i = 0;

            for (m = 0; m < 8; m++) {
                for (n = 3; n < 5; n++) {
                    if (i > itemStacks.size() - 1) {
                        return build;
                    }
                    build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(m, n))).offer(itemStacks.get(i));
                    i++;
                }
            }
        }
        return build;

    }


    private static List<ItemStack> configurationToItemStacks(SkillData skillData) {
        List<ItemStack> a = new ArrayList<>();
        if (skillData.getSkillSettings() != null) {
            Map<String, Float> nodes = skillData.getSkillSettings().getNodes();
            for (Map.Entry<String, Float> s : nodes.entrySet()) {
                if (!s.getKey().endsWith("_levelbonus")) {
                    String s1 = Utils.configNodeToReadableString(s.getKey());
                    Float init = s.getValue();
                    Float lbonus = nodes.get(s.getKey() + "_levelbonus");
                    ItemStack of = GuiHelper.itemStack(ItemTypes.PAPER);
                    of.offer(Keys.DISPLAY_NAME, Text.builder(s1).build());
                    of.offer(new MenuInventoryData(true));
                    of.offer(Keys.ITEM_LORE, Arrays.asList(
                            Text.builder().append(translate(LocalizationKeys.SKILL_VALUE_STARTS_AT))
                                    .append(Text.builder(": " + init)
                                            .color(TextColors.GREEN).style(TextStyles.BOLD)
                                            .build())
                                    .build()
                            ,
                            Text.builder().append(translate(LocalizationKeys.SKILL_VALUE_PER_LEVEL))
                                    .style(TextStyles.BOLD).color(TextColors.GOLD)
                                    .append(Text.builder(": " + lbonus)
                                            .color(TextColors.GREEN).style(TextStyles.BOLD)
                                            .build())
                                    .build()
                    ));
                    a.add(of);
                }
            }
        }
        return a;
    }


    public static ItemStack interactiveModeToitemStack(ISpongeCharacter character, SkillTreeViewModel.InteractiveMode interactiveMode) {

        String translation = null;
        ItemType itemType = null;

        switch (interactiveMode) {
            case FAST:
                translation = LocalizationKeys.INTERACTIVE_SKILLTREE_MOD_FAST;
                itemType = ItemTypes.GOLD_NUGGET;
                break;
            case DETAILED:
                translation = LocalizationKeys.INTERACTIVE_SKILLTREE_MOD_DETAILS;
                itemType = ItemTypes.BOOK;
                break;
        }
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        Text interactiveModeName = TextHelper.parse(localizationService.translate(translation));
        ItemStack md = itemStack(itemType);
        List<Text> lore = new ArrayList<>();

        md.offer(new SkillTreeInventoryViewControllsData(SkillTreeControllsButton.MODE));
        md.offer(new MenuInventoryData(true));
        lore.add(interactiveModeName);
        lore.add(Text.EMPTY);
        lore.add(Text.builder("Level: ").color(TextColors.YELLOW)
                .append(Text.builder(String.valueOf(character.getLevel())).style(TextStyles.BOLD).build())
                .build());

        ClassDefinition viewedClass = character.getLastTimeInvokedSkillTreeView().getViewedClass();
        CharacterClass characterClass = character.getCharacterBase().getCharacterClass(viewedClass);
        if (characterClass == null) {
            String translate = localizationService.translate(LocalizationKeys.CLASS_NOT_SELECTED);
            lore.add(TextHelper.parse(translate));
        } else {
            int sp = characterClass.getSkillPoints();

            lore.add(Text.builder("SP: ").color(TextColors.GREEN)
                    .append(Text.builder(String.valueOf(sp)).style(TextStyles.BOLD).build())
                    .build());
        }
        md.offer(Keys.ITEM_LORE, lore);
        return md;
    }

    public static ItemStack rpgItemTypeToItemStack(SpongeRpgItemType configRPGItemType) {
        ItemStack q = itemStack(configRPGItemType.getItemType());
        Text lore = Text.builder().append(translate(LocalizationKeys.ITEM_DAMAGE))
                .append(Text.builder(": " + configRPGItemType.getDamage())
                        .style(TextStyles.BOLD)
                        .color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(configRPGItemType.getDamage()))
                        .build())
                .build();
        q.offer(Keys.ITEM_LORE, Collections.singletonList(lore));
        q.offer(new MenuInventoryData(true));
        if (configRPGItemType.getModelId() != null) {
            q.offer(Keys.DISPLAY_NAME, Text.of(configRPGItemType.getModelId()));
        }
        return q;
    }

    public static ItemStack toItemStack(ClassDefinition a) {
        String sItemType = a.getItemType();
        ItemType type = Sponge.getRegistry().getType(ItemType.class, sItemType).orElse(ItemTypes.STONE);
        ItemStack itemStack = itemStack(type);
        itemStack.offer(Keys.DISPLAY_NAME, Text.builder(a.getName()).color(toTextColor(a.getPreferedColor())).style(TextStyles.BOLD).build());

        if (a.getCustomLore().isEmpty()) {
            itemStack.offer(Keys.ITEM_LORE, a.getCustomLore().stream().map(TextHelper::parse).collect(Collectors.toList()));
        } else {
            List<Text> lore = new ArrayList<>();
            String description = a.getDescription();
            lore.add(Text.builder(a.getClassType()).style(TextStyles.BOLD).color(TextColors.GRAY).build());
            lore.add(Text.EMPTY);
            lore.add(Text.builder(description).style(TextStyles.ITALIC).color(TextColors.GOLD).build());
            itemStack.offer(Keys.ITEM_LORE, lore);
        }
        itemStack.offer(new InventoryCommandItemMenuData("class " + a.getName()));
        itemStack.offer(new MenuInventoryData(true));
        return itemStack;
    }

    public static void makeBorder(Inventory i, DyeColor dyeColor) {
        if (i.getArchetype() == InventoryArchetypes.DOUBLE_CHEST) {
            for (int j = 0; j < 9; j++) {
                ItemStack of = unclickableInterface(dyeColor);
                i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(j, 0))).offer(of);

                of = unclickableInterface(dyeColor);
                i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(j, 5))).offer(of);
            }

            for (int j = 1; j < 5; j++) {
                ItemStack of = unclickableInterface(dyeColor);
                i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, j))).offer(of);

                of = unclickableInterface(dyeColor);
                i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, j))).offer(of);
            }
        }
    }

    public static Inventory.Builder createCharacterEmptyInventory(IActiveCharacter character) {
        return Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.of(character.getCharacterBase().getName(), TextStyles.BOLD)))
                ;
    }

    public static ItemStack itemStack(String itemType) {
        if (itemType == null) {
            return itemStack(ItemTypes.STONE);
        }
        return itemStack(Sponge.getRegistry().getType(ItemType.class, itemType).orElse(ItemTypes.STONE));
    }

    private static DyeColor toDyeColor(String id) {
        return Sponge.getRegistry().getType(DyeColor.class, id).orElseGet(() -> {
            Log.warn("Unknown text color " + id);
            return DyeColors.WHITE;
        });
    }

    private static TextColor toTextColor(String id) {
        return Sponge.getRegistry().getType(TextColor.class, id).orElseGet(() -> {
            Log.warn("Unknown text color " + id);
            return TextColors.WHITE;
        });
    }

    private static Text translate(String id) {
        return TextHelper.parse(Rpg.get().getLocalizationService().translate(id));
    }
}
