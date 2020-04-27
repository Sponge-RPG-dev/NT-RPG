package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.gui.ConfigInventory;
import cz.neumimto.rpg.common.gui.DynamicInventory;
import cz.neumimto.rpg.common.gui.TemplateInventory;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.inventory.data.InventoryCommandItemMenuData;
import cz.neumimto.rpg.sponge.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.sponge.inventory.data.SkillTreeInventoryViewControllsData;
import cz.neumimto.rpg.sponge.listeners.SkillTreeInventoryListener;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.utils.TextHelper;
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

    public static Text JOINT = Text.of(TextColors.DARK_GRAY, "[", TextColors.DARK_RED, "+", TextColors.DARK_GRAY, "]");
    public static Text HEADER_START = Text.of(TextColors.DARK_GRAY, "════════ [ ");
    public static Text HEADER_END = Text.of(TextColors.DARK_GRAY, " ] ════════");
    public static Text VERTICAL_LINE = Text.of(TextColors.DARK_GRAY, "║ ", TextColors.GRAY);
    public static Set<String> SKILL_SETTINGS_DURATION_NODES = new HashSet<>();

    public static Map<String, Inventory> CACHED_MENUS = new HashMap<>();
    public static Map<String, ConfigInventory<ItemStack, Inventory>> CACHED_MENU_TEMPLATES = new HashMap<>();

    static {
        SKILL_SETTINGS_DURATION_NODES.add(SkillNodes.DURATION.value());
        SKILL_SETTINGS_DURATION_NODES.add(SkillNodes.PERIOD.value());
        SKILL_SETTINGS_DURATION_NODES.add(SkillNodes.COOLDOWN.value());


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

    public static void initInventories() {
        CACHED_MENU_TEMPLATES.clear();
        CACHED_MENUS.clear();
        Map<String, Object> stringObjectMap = new SpongeUIReader().initInventories();
        for (Map.Entry<String, Object> next : stringObjectMap.entrySet()) {
            if (next.getValue() instanceof Inventory) {
                CACHED_MENUS.put(next.getKey(), (Inventory) next.getValue());
            } else {
                CACHED_MENU_TEMPLATES.put(next.getKey(), (ConfigInventory<ItemStack, Inventory>) next.getValue());
            }
        }
    }


    public static Text header(String header) {
        return header(header, TextColors.GREEN);
    }

    public static Text header(String header, TextColor textColor) {
        return Text.of(JOINT, HEADER_START, textColor, header, HEADER_END);
    }

    public static Text line(String line) {
        return Text.of(VERTICAL_LINE, TextColors.GRAY, line);
    }

    public static Text node(String key, String value) {
        return Text.of(VERTICAL_LINE, TextColors.GRAY, Rpg.get().getLocalizationService().translate(key), TextColors.DARK_GRAY, ": " + value);
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

    public static Inventory createMenuInventoryClassTypesView() {
        return CACHED_MENUS.get("class_types");
    }

    public static Inventory createMenuInventoryClassesByTypeView(String classType) {
        return CACHED_MENUS.get("classes_by_type" + classType);
    }

    public static ItemStack propertyToItemStack(int id, float value) {
        ItemStack i = itemStack(ItemTypes.BOOK);
        String nameById = Rpg.get().getPropertyService().getNameById(id);
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
        command(command, of);
        of.offer(Keys.DISPLAY_NAME, displayName);
        return of;
    }

    public static ItemStack command(String command, ItemStack of) {
        of.offer(new MenuInventoryData(true));
        of.offer(new InventoryCommandItemMenuData(command));
        return of;
    }

    public static ItemStack back(ClassDefinition g) {
        return command("ninfo class " + g.getName(), translate(LocalizationKeys.BACK), ItemTypes.PAPER);
    }

    public static ItemStack unclickableInterface(DyeColor dyeColor) {
        ItemStack of = itemStack(ItemTypes.STAINED_GLASS_PANE);
        of.offer(new MenuInventoryData(true));
        of.offer(Keys.DYE_COLOR, dyeColor);
        of.offer(Keys.DISPLAY_NAME, Text.EMPTY);
        return of;
    }

    public static ItemStack unclickableInterface(ItemStack of) {
        of.offer(new MenuInventoryData(true));
        of.offer(Keys.DISPLAY_NAME, Text.EMPTY);
        return of;
    }

    static ItemStack skillToItemStack(ISpongeCharacter character, SkillData skillData, SkillTree skillTree, SpongeSkillTreeViewModel model) {
        return toItemStack(skillData.getSkill(), character, skillData, skillTree, model);
    }

    public static Inventory createCharacterMenu(ISpongeCharacter cc) {
        String name = "char_view" + cc.getName();
        Inventory dynamicInventory = CACHED_MENUS.get(name);
        if (dynamicInventory == null) {
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_view");
            ItemStack[] chars = cc.getClasses().values()
                    .stream()
                    .map(PlayerClassData::getClassDefinition)
                    .map(GuiHelper::toItemStack)
                    .collect(Collectors.toList())
                    .toArray(new ItemStack[cc.getClasses().size() == 0 ? 0 : cc.getClasses().size() - 1]);
            DynamicInventory inv = dView.setActualContent(chars);
            dynamicInventory = createInventoryTemplate(cc.getName());
            inv.fill(dynamicInventory);
            CACHED_MENUS.put(name, dynamicInventory);
        }
        return dynamicInventory;
    }


    private static ItemStack toItemStack(ISkill skill, ISpongeCharacter character, SkillData skillData, SkillTree skillTree, SpongeSkillTreeViewModel model) {
        List<Text> lore;
        TextColor nameColor;
        Pair<List<Text>, TextColor> fromCache = model.getFromCache(skill);

        if (fromCache == null) {
            lore = new ArrayList<>();
            nameColor = getSkillTextColor(character, skill, skillData, skillTree);

            if (skillData.useDescriptionOnly()) {
                List<String> description = skillData.getDescription(character);
                for (String s : description) {
                    lore.add(TextHelper.parse(s));
                }
            } else {
                lore = toLore(character, skillData, nameColor);
            }

            model.addToCache(skill, lore, nameColor);
        } else {
            lore = fromCache.key;
            nameColor = fromCache.value;
        }
        ItemStack itemStack = itemStack(skillData.getIcon());
        itemStack.offer(Keys.DISPLAY_NAME, Text.builder(skillData.getSkillName()).style(TextStyles.BOLD).color(nameColor).build());
        itemStack.offer(Keys.ITEM_LORE, lore);
        return itemStack;
    }

    public static List<Text> toLore(ISpongeCharacter character, SkillData skillData, TextColor nameColor) {
        ISkill skill = skillData.getSkill();
        List<Text> lore = new ArrayList<>();
        if (skillData.useDescriptionOnly()) {
            List<String> description = skillData.getDescription(character);
            for (String s : description) {
                lore.add(TextHelper.parse(s));
            }
        } else {
            LocalizationService locService = Rpg.get().getLocalizationService();
            lore.add(header(locService.translate(skillData.getSkillName()), nameColor));
            lore.add(node(locService.translate(LocalizationKeys.SKILL_EXECUTION_TYPE), locService.translate(skill.getSkillExecutionType().toString().toLowerCase())));

            PlayerSkillContext psc = character.getSkillInfo(skill);
            String level = psc == null ? " -- " : psc.getLevel() + (psc.getLevel() != psc.getTotalLevel() ? " (" + psc.getTotalLevel() + ")" : "");
            lore.add(node(locService.translate(LocalizationKeys.LEVEL), level));
            lore.add(node(locService.translate(LocalizationKeys.SKILL_MAX_LEVEL), "" + skillData.getMaxSkillLevel()));
            if (skillData.getMinPlayerLevel() > 0) {
                lore.add(node(locService.translate(LocalizationKeys.SKILL_MIN_CLASS_LEVEL), "" + skillData.getMinPlayerLevel()));
            }
            if (skillData.getLevelGap() > 0) {
                lore.add(node(locService.translate(LocalizationKeys.SKILL_LEVEL_GAP), "" + skillData.getLevelGap()));
            }


            SkillSettings skillSettings = skillData.getSkillSettings();
            lore.add(header(locService.translate(LocalizationKeys.SKILL_SETTINGS)));

            String value = null;
            for (Map.Entry<String, Float> entry : skillSettings.getNodes().entrySet()) {
                if (entry.getKey().endsWith(SkillSettings.BONUS_SUFFIX) || entry.getKey().contains("_per_")) {
                    continue;
                }

                String translatedNode = locService.translate(entry.getKey());
                Float bonusNode = skillSettings.getNodes().get(translatedNode + SkillSettings.BONUS_SUFFIX);

                if (SKILL_SETTINGS_DURATION_NODES.contains(translatedNode)) {
                    value = String.format("%.2f", entry.getValue() * 0.001) + " ms";
                    if (bonusNode != null && bonusNode != 0) {
                        value += " (" + String.format("%.2f", bonusNode * 0.001) + " ms)";
                    }
                } else {
                    value = String.format("%.2f", entry.getValue());
                    if (bonusNode != null && bonusNode != 0) {
                        value += " (" + String.format("%.2f", bonusNode) + ")";
                    }
                }

                if (entry.getValue() == 0f && (bonusNode == null || bonusNode == 0f)) {
                    continue;
                }
                lore.add(node(translatedNode, value));
            }

            Map<AttributeConfig, SkillSettings.AttributeSettings> attributeSettings = skillSettings.getAttributeSettings();
            if (attributeSettings.size() > 0) {
                lore.add(header(locService.translate(LocalizationKeys.SKILL_ATTRIBUTE_SETTINGS)));

                for (Map.Entry<AttributeConfig, SkillSettings.AttributeSettings> e : attributeSettings.entrySet()) {
                    float value1 = e.getValue().value;
                    String strVal = null;
                    if (value1 == 0f) {
                        continue;
                    }
                    if (SKILL_SETTINGS_DURATION_NODES.contains(e.getValue().node)) {
                        strVal = String.format("%.2f", value1 * 0.001) + " ms";
                    } else {
                        strVal = String.valueOf(value1);
                    }
                    String line = locService.translate(LocalizationKeys.SKILL_ATTRIBUTE_SETTING_PATTERN,
                            Arg.arg("value", strVal)
                                    .with("attr", e.getKey().getName())
                                    .with("node", locService.translate(e.getValue().node)));
                    lore.add(line(line));
                }
            }

            List<String> description = skillData.getDescription(character);
            if (description != null && description.size() > 0) {
                lore.add(header(locService.translate(LocalizationKeys.DESCRIPTION)));

                for (String s : description) {
                    lore.add(line(s));
                }
            }

            lore.add(header(locService.translate(LocalizationKeys.SKILL_TRAITS)));
            Set<ISkillType> skillTypes = skill.getSkillTypes();
            StringBuilder builder = new StringBuilder();
            Iterator<ISkillType> iterator = skillTypes.iterator();
            int i = 0;
            boolean firstLine = true;
            while (iterator.hasNext()) {
                i++;
                ISkillType next = iterator.next();
                String translate = locService.translate(next.toString()) + " ";
                builder.append(translate);
                if (i % 4 == 0) {
                    if (firstLine) {
                        lore.add(node(locService.translate(LocalizationKeys.SKILL_TYPES), builder.toString()));
                    } else {
                        lore.add(line(" - " + builder.toString()));
                    }

                    builder = new StringBuilder();
                    firstLine = false;
                }
            }
        }


        return lore;
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
                .build(SpongeRpgPlugin.getInstance());

        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 0))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 1))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 2))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 3))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 4))).offer(unclickableInterface(DyeColors.YELLOW));
        i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 5))).offer(unclickableInterface(DyeColors.YELLOW));


        SpongeSkillTreeViewModel model = character.getSkillTreeViewLocation().get(skillTree.getId());

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

    public static Inventory createSkillDetailInventoryView(ISpongeCharacter character, SkillTree skillTree, SkillData skillData) {
        Inventory build = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.builder(skillData.getSkillName()).color(TextColors.DARK_GREEN).style(TextStyles.BOLD)
                        .build()))
                .build(SpongeRpgPlugin.getInstance());

        SpongeSkillTreeViewModel skillTreeViewModel = character.getLastTimeInvokedSkillTreeView();
        ItemStack back = back("skilltree " + skillTreeViewModel.getViewedClass().getName(), translate(LocalizationKeys.SKILLTREE));
        build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back);

        if (skillData instanceof SkillPathData) {
            SkillPathData data = (SkillPathData) skillData;

            ItemStack of = itemStack(ItemTypes.PAPER);
            of.offer(Keys.DISPLAY_NAME, Text.of("Tier " + data.getTier()));
            of.offer(new MenuInventoryData(true));
            build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 0))).offer(of);

            SkillService skillService = Rpg.get().getSkillService();

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


    public static ItemStack interactiveModeToitemStack(ISpongeCharacter character, SpongeSkillTreeViewModel.InteractiveMode interactiveMode) {

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

    public static ItemStack toItemStack(ClassDefinition a) {
        String sItemType = a.getItemType();
        ItemType type = Sponge.getRegistry().getType(ItemType.class, sItemType).orElse(ItemTypes.STONE);
        ItemStack itemStack = itemStack(type);

        List<Text> lore = new ArrayList<>();
        lore.add(header(a.getName()));
        lore.add(node(LocalizationKeys.CLASS_TYPE, a.getClassType()));

        if (a.getDescription() != null && a.getDescription().size() > 0) {
            List<String> description = a.getDescription();
            String descriptionS = Rpg.get().getLocalizationService().translate(LocalizationKeys.DESCRIPTION);
            lore.add(header(descriptionS));

            for (String s : description) {
                lore.add(line(s));
            }
        }

        if (a.getCustomLore() != null && a.getCustomLore().size() > 0) {
            String loreH = Rpg.get().getLocalizationService().translate(LocalizationKeys.LORE);
            lore.add(header(loreH));

            List<String> ll = a.getCustomLore();
            for (String s : ll) {
                lore.add(line(s));
            }

        }
        itemStack.offer(Keys.ITEM_LORE, lore);
        itemStack.offer(new InventoryCommandItemMenuData("ninfo class " + a.getName()));
        itemStack.offer(new MenuInventoryData(true));
        return itemStack;
    }

    public static ItemStack itemStack(String itemType) {
        if (itemType == null) {
            return itemStack(ItemTypes.STONE);
        }
        return itemStack(Sponge.getRegistry().getType(ItemType.class, itemType).orElse(ItemTypes.STONE));
    }

    private static Text translate(String id) {
        return TextHelper.parse(Rpg.get().getLocalizationService().translate(id));
    }

    public static Inventory createInventoryTemplate(String title) {
        return Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(Text.of(title)))
                .build(SpongeRpgPlugin.getInstance());
    }

    public static Inventory getCharacterAllowedArmor(ISpongeCharacter character, int page) {
        String name = "char_allowed_items_armor" + character.getName();
        Inventory inventory = CACHED_MENUS.get(name);
        if (inventory == null) {
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_allowed_items");
            Map<RpgItemType, Double> allowedWeapons = character.getAllowedWeapons();
            List<ItemStack> content = new ArrayList<>();
            for (Map.Entry<RpgItemType, Double> ent : allowedWeapons.entrySet()) {
                RpgItemType key = ent.getKey();
                Double value = ent.getValue();
                ItemStack is = toItemStack(key, value);
                content.add(is);
            }
            DynamicInventory inv = dView.setActualContent(content.toArray(new ItemStack[content.size() == 0 ? 0 : content.size() - 1]));
            String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.CHARACTER_ARMOR);
            inventory = createInventoryTemplate(translate);
            inv.fill(inventory);
            CACHED_MENUS.put(name, inventory);
        }
        return inventory;
    }

    public static Inventory getCharacterAllowedWeapons(ISpongeCharacter character, int page) {
        String name = "char_allowed_items_weapons" + character.getName();
        Inventory inventory = CACHED_MENUS.get(name);
        if (inventory == null) {
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_allowed_items");
            Set<RpgItemType> allowedWeapons = character.getAllowedArmor();
            List<ItemStack> content = new ArrayList<>();
            for (RpgItemType ent : allowedWeapons) {
                ItemStack is = toItemStack(ent, 0);
                content.add(is);
            }
            DynamicInventory inv = dView.setActualContent(content.toArray(new ItemStack[content.size() == 0 ? 0 : content.size() - 1]));
            String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.CHARACTER_ARMOR);
            inventory = createInventoryTemplate(translate);
            inv.fill(inventory);
            CACHED_MENUS.put(name, inventory);
        }
        return inventory;
    }

    private static ItemStack toItemStack(RpgItemType key, double damage) {
        String id = key.getId();
        Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, id);
        List<Text> lore = new ArrayList<>();
        ItemStack is = ItemStack.of(type.get());

        LocalizationService localizationService = Rpg.get().getLocalizationService();
        String translate = localizationService.translate(LocalizationKeys.ITEM_CLASS);
        lore.add(Text.of(TextColors.GRAY, translate, ": ", TextColors.GREEN, key.getItemClass().getName()));
        if (damage > 0) {
            translate = localizationService.translate(LocalizationKeys.ITEM_DAMAGE);
            lore.add(Text.of(TextColors.GRAY, translate, ": ", TextColors.RED, damage));
        }
        if (Rpg.get().getPluginConfig().DEBUG.isBalance()) {
            //tood 1.15
        }

        is.offer(new MenuInventoryData(true));
        is.offer(Keys.ITEM_LORE, lore);
        return is;
    }

}
