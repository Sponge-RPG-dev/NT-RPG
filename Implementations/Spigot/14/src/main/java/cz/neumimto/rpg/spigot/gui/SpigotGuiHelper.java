package cz.neumimto.rpg.spigot.gui;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.SkillTreeViewModel;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.gui.ConfigInventory;
import cz.neumimto.rpg.common.gui.DynamicInventory;
import cz.neumimto.rpg.common.gui.TemplateInventory;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.items.SpigotRpgItemType;
import cz.neumimto.rpg.spigot.skills.SpigotSkillService;
import cz.neumimto.rpg.spigot.skills.SpigotSkillTreeInterfaceModel;
import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SpigotGuiHelper {

    static int[] inventoryIds;
    private static final int[] attributButtonSlots;

    public static ItemLoreFactory itemLoreFactory;

    public static Map<String, Inventory> CACHED_MENUS = new HashMap<>();
    public static Map<String, ConfigInventory<ItemStack, Inventory>> CACHED_MENU_TEMPLATES = new HashMap<>();

    static {
        itemLoreFactory = new ItemLoreFactory();
        List<Integer> ids = new ArrayList<>();
        ids.add(0);
        int k = 0;
        for (int j = 1; j < 53; j++) {
            if (!(j % 9 == 0) && (j - k) % 8 == 0) {
                k++;
                continue;
            }
            ids.add(j);
        }
        inventoryIds = ids.stream().mapToInt(a -> a).toArray();

        attributButtonSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 36, 37, 38, 39, 40, 41, 42, 43, 45};

    }

    public static void initInventories() {
        Map<String, Object> stringObjectMap = new SpigotUIReader().initInventories();
        for (Map.Entry<String, Object> next : stringObjectMap.entrySet()) {
            if (next.getValue() instanceof Inventory) {
                CACHED_MENUS.put(next.getKey(), (Inventory) next.getValue());
            } else {
                CACHED_MENU_TEMPLATES.put(next.getKey(), (ConfigInventory<ItemStack, Inventory>) next.getValue());
            }
        }
    }

    public static Inventory createMenuInventoryClassTypesView(Player player) {
        return CACHED_MENUS.get("class_types");
    }

    public static Inventory createMenuInventoryClassesByTypeView(Player player, String classType) {
        return CACHED_MENUS.get("classes_by_type" + classType);
    }

    public static Inventory createCharacterMenu(ISpigotCharacter cc) {
        String name = "char_view" + cc.getName();
        Inventory dynamicInventory = CACHED_MENUS.get(name);
        if (dynamicInventory == null) {
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_view");
            ItemStack[] chars = cc.getClasses().values()
                    .stream()
                    .map(PlayerClassData::getClassDefinition)
                    .map(a -> toItemStack(a, "char"))
                    .collect(Collectors.toList())
                    .toArray(new ItemStack[cc.getClasses().size() == 0 ? 0 : cc.getClasses().size() - 1]);
            DynamicInventory inv = dView.setActualContent(chars);
            dynamicInventory = createInventoryTemplate(cc.getName());
            inv.fill(dynamicInventory);
            CACHED_MENUS.put(name, dynamicInventory);
        }
        return dynamicInventory;
    }

    public static ItemStack toItemStack(ClassDefinition a, String backCommand) {
        String sItemType = a.getItemType();
        Material material = Material.matchMaterial(sItemType);
        ItemStack itemStack = button(material, ChatColor.valueOf(a.getPreferedColor()) + a.getName(), "ninfo class " + a.getName() + " " + backCommand);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(itemLoreFactory.toLore(a));

        if (a.getItemModel() != null) {
            itemMeta.setCustomModelData(a.getItemModel());
        }

        itemStack.setItemMeta(itemMeta);
        itemStack = unclickableInterface(itemStack);

        return itemStack;
    }

    public static Inventory createInventoryTemplate(Player player, String title) {
        return Bukkit.createInventory(player, 6 * 9, title);
    }

    public static Inventory createInventoryTemplate(String title) {
        return Bukkit.createInventory(null, 6 * 9, title);
    }

    private static ItemStack button(Resourcepack.RPItem i, String name, String command) {
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        return button(i.mat, name, localizationService.translate(command), i.model);
    }


    private static ItemStack button(Material material, String name, String command) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        NBTItem nbti = new NBTItem(itemStack);
        nbti.setString("ntrpg.item-command", command);
        return nbti.getItem();
    }

    public static ItemStack button(Material material, String name, String command, Integer data) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setDisplayName(name);
        if (data != null) {
            itemMeta.setCustomModelData(data);
        }
        itemStack.setItemMeta(itemMeta);
        NBTItem nbti = new NBTItem(itemStack);
        nbti.setString("ntrpg.item-command", command);
        return nbti.getItem();
    }

    private static ItemStack unclickableInterface(Material material) {
        ItemStack itemStack = new ItemStack(material);
        return unclickableInterface(itemStack);
    }

    public static ItemStack unclickableInterface(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
        return unclickableIcon(itemStack);
    }

    public static ItemStack unclickableIcon(ItemStack itemStack) {
        NBTItem nbti = new NBTItem(itemStack);
        nbti.setBoolean("ntrpg.item-iface", true);
        return nbti.getItem();
    }

    public static ItemStack unclickableInterface(Material material, int model) {
        ItemStack itemStack = new ItemStack(material);
        return unclickableInterface(itemStack, model);
    }

    private static ItemStack unclickableInterface(ItemStack itemStack, int model) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setCustomModelData(model);
        itemStack.setItemMeta(itemMeta);
        NBTItem nbti = new NBTItem(itemStack);
        nbti.setBoolean("ntrpg.item-iface", true);
        return nbti.getItem();
    }

    public static void sendcharacters(Player player, ISpigotCharacter player1, CharacterBase currentlyCreated) {
        CompletableFuture.runAsync(() -> {
            List<CharacterBase> playersCharacters = Rpg.get().getCharacterService().getPlayersCharacters(player.getUniqueId());


            for (CharacterBase base : playersCharacters) {
                ComponentBuilder builder = new ComponentBuilder("[")
                        .color(net.md_5.bungee.api.ChatColor.YELLOW);
                if (base.getName().equalsIgnoreCase(currentlyCreated.getName())) {
                    builder.append("*").color(net.md_5.bungee.api.ChatColor.RED);
                } else {
                    builder.append("SELECT").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/character switch " + base.getName()))
                            .color(net.md_5.bungee.api.ChatColor.GREEN);
                }

                builder.append("] ").color(net.md_5.bungee.api.ChatColor.YELLOW)
                        .append(base.getName() + " ").color(net.md_5.bungee.api.ChatColor.GOLD)
                        .append(base.getCharacterClasses().stream().map(CharacterClass::getName).collect(Collectors.joining(", ")))
                        .color(net.md_5.bungee.api.ChatColor.GRAY);

                player.spigot().sendMessage(builder.create());
            }


        }, Rpg.get().getAsyncExecutor()).exceptionally(throwable -> {
            Log.error("Could not get character list", throwable);
            return null;
        });
    }

    public static Inventory createClassAttributesView(Player player, ClassDefinition cc) {
        String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.ATTRIBUTES);
        Map<AttributeConfig, Integer> attrs = cc.getStartingAttributes();
        Inventory i = createInventoryTemplate(player, ChatColor.valueOf(cc.getPreferedColor()) + cc.getName() + ChatColor.RESET + translate);

        i.setItem(0, button(Material.PAPER, Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK), "ninfo class " + cc.getName()));

        int w = 9;


        for (Map.Entry<AttributeConfig, Integer> attr : attrs.entrySet()) {
            AttributeConfig att = attr.getKey();
            ItemStack itemStack = new ItemStack(Material.matchMaterial(att.getItemType()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + att.getName() + ChatColor.RESET + " - " + ChatColor.GREEN + attr.getValue());
            lore.add(" ");
            if (att.getDescription() != null || !att.getDescription().isEmpty()) {
                lore.add(ChatColor.ITALIC.toString() + ChatColor.GOLD + att.getDescription());
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            i.setItem(w, unclickableInterface(itemStack));
            w++;
        }

        return i;
    }

    public static Inventory createSkillTreeView(ISpigotCharacter character, SkillTree skillTree) {
        Player player = character.getPlayer();
        Inventory i = createInventoryTemplate(player, Rpg.get().getLocalizationService().translate(LocalizationKeys.SKILLTREE));
        fillSkillTreeViewInterface(i);
        return i;
    }

    private static void fillSkillTreeViewInterface(Inventory i) {

        i.setItem(26, button(Resourcepack.UP, "Up", "skilltree north"));
        i.setItem(35, button(Resourcepack.DOWN, "Down", "skilltree south"));
        i.setItem(53, button(Resourcepack.LEFT, "Left", "skilltree east"));
        i.setItem(44, button(Resourcepack.RIGHT, "Right", "skilltree west"));
    }

    public static Inventory drawSkillTreeViewData(Inventory i, ISpigotCharacter character) {
        SpigotSkillTreeViewModel skillTreeViewModel = character.getLastTimeInvokedSkillTreeView();
        SkillTree skillTree = skillTreeViewModel.getSkillTree();
        short[][] skillTreeMap = skillTreeViewModel.getSkillTree().getSkillTreeMap();
        int centerY = skillTree.getCenter().value + skillTreeViewModel.getLocation().value; //y
        int centerX = skillTree.getCenter().key + skillTreeViewModel.getLocation().key; //x

        if (skillTreeMap == null) {
            throw new IllegalStateException("No AsciiMap defined for skilltree: " + skillTree.getId());
        }

        int columns = skillTreeMap[0].length;
        int rows = skillTreeMap.length;

        SpigotSkillTreeViewModel.InteractiveMode interactiveMode = skillTreeViewModel.getInteractiveMode();
        ItemStack md = interactiveModeToitemStack(character, interactiveMode);
        i.setItem(8, md);

        SpigotSkillService skillService = (SpigotSkillService) Rpg.get().getSkillService();

        ItemStack blank = blank();
        ItemStack boundary = skillTreeBoundary();
        int y = -4;
        int x = -3;
        for (int slotId : inventoryIds) {
            i.setItem(slotId, new ItemStack(Material.RED_STAINED_GLASS_PANE));
            if (slotId % 9 == 0) {
                y++;
                x = -4;
            } else {
                x++;
            }
            int realX = centerX + x;
            int realY = centerY + y;

            if (isInRange(skillTreeMap, realX, realY) && realX < columns && realY < rows) {

                short id = skillTreeMap[realY][realX];
                if (id > 0) {
                    i.setItem(slotId, getIcon(character, skillTreeViewModel, skillTree, skillService, id));
                } else {
                    i.setItem(slotId, blank);
                }
            } else {
                i.setItem(slotId, boundary);
            }

        }
        return i;
    }

    private static boolean isInRange(short[][] array, int indexX, int indexY) {
        return indexX >= 0 && indexY >= 0;
    }

    private static ItemStack skillTreeBoundary() {
        return unclickableInterface(Material.RED_STAINED_GLASS_PANE, 1235);
    }

    private static ItemStack getIcon(ISpigotCharacter character, SpigotSkillTreeViewModel skillTreeViewModel, SkillTree skillTree, SpigotSkillService skillService, short id) {
        ItemStack itemStack;
        SpigotSkillTreeInterfaceModel guiModelById = skillService.getGuiModelById(id);
        if (guiModelById != null) {
            itemStack = guiModelById.toItemStack();
        } else {
            SkillData skillById = skillTree.getSkillById(id);

            if (skillById == null) {
                itemStack = unclickableInterface(Material.BARRIER);
            } else {
                itemStack = skillToItemStack(character, skillById, skillTree, skillTreeViewModel);
            }
        }
        return itemStack;
    }

    private static ItemStack blank() {
        return unclickableInterface(Material.GRAY_STAINED_GLASS_PANE, 1235);
    }


    private static ItemStack skillToItemStack(ISpigotCharacter character, SkillData skillData, SkillTree skillTree, SpigotSkillTreeViewModel model) {
        List<String> lore;

        ISkill skill = skillData.getSkill();
        ChatColor nameColor = getSkillTextColor(character, skill, skillData, skillTree);

        List<String> fromCache = model.getFromCache(skill);

        if (fromCache == null) {
            lore = itemLoreFactory.toLore(character, skillData, nameColor, skillTree);
            model.addToCache(skill, lore);
        } else {
            lore = fromCache;
        }

        Material material;
        if (skillData.getIcon() != null) {
            material = Material.matchMaterial(skillData.getIcon());
        } else {
            material = Material.STONE;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.values());
        if (skillData.getModelId() != null) {
            itemMeta.setCustomModelData(skillData.getModelId());
        }
        itemStack.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("ntrpg.item-command", "skilltree skill " + skill.getId());
        return nbtItem.getItem();
    }

    private static ItemStack interactiveModeToitemStack(ISpigotCharacter character, SkillTreeViewModel.InteractiveMode interactiveMode) {
        String translation = null;
        Material itemType = null;

        switch (interactiveMode) {
            case FAST:
                translation = LocalizationKeys.INTERACTIVE_SKILLTREE_MOD_FAST;
                itemType = Material.GOLD_NUGGET;
                break;
            case DETAILED:
                translation = LocalizationKeys.INTERACTIVE_SKILLTREE_MOD_DETAILS;
                itemType = Material.BOOK;
                break;
        }
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        String interactiveModeName = localizationService.translate(translation);
        ItemStack md = new ItemStack(itemType);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Level: " + ChatColor.RESET + ChatColor.BOLD + character.getLevel());

        ClassDefinition viewedClass = character.getLastTimeInvokedSkillTreeView().getViewedClass();
        CharacterClass characterClass = character.getCharacterBase().getCharacterClass(viewedClass);
        if (characterClass == null) {
            String translate = localizationService.translate(LocalizationKeys.CLASS_NOT_SELECTED);
            lore.add(translate);
        } else {
            int sp = characterClass.getSkillPoints();
            lore.add(ChatColor.GREEN + "SP: " + ChatColor.RESET + ChatColor.BOLD + sp);
        }

        ItemMeta itemMeta = md.getItemMeta();
        itemMeta.setDisplayName(interactiveModeName);
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(1234);
        md.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(md);
        nbtItem.setString("ntrpg.item-command", "skilltree mode");
        return nbtItem.getItem();
    }

    private static ChatColor getSkillTextColor(IActiveCharacter character, ISkill skill, SkillData skillData, SkillTree skillTree) {
        if (character.hasSkill(skillData.getSkillId())) {
            return ChatColor.GREEN;
        }
        Collection<PlayerClassData> values = character.getClasses().values();
        Optional<PlayerClassData> first = values.stream().filter(a -> a.getClassDefinition().getSkillTree() == skillTree).findFirst();
        return first.filter(playerClassData -> Rpg.get().getCharacterService().canLearnSkill(character, playerClassData.getClassDefinition(), skill).isOk()).map(playerClassData -> ChatColor.GRAY).orElse(ChatColor.RED);
    }

    public static void refreshCharacterAttributeView(Player player, ISpigotCharacter character, Inventory i, int slotMod, AttributeConfig a) {
        String id = a.getId();
        int transientVal = character.getTransientAttributes().get(id);
        int real = character.getCharacterBase().getAttributes().get(id);
        int tx = character.getTransientAttributes().get(id);
        ItemStack atris = charAttributeToItemStack(a, real, transientVal, tx);
        i.setItem(slotMod, atris);
    }


    public static Inventory createCharacterAttributeView(Player player, ISpigotCharacter character) {
        Inventory i = createInventoryTemplate(player, Rpg.get().getLocalizationService().translate(LocalizationKeys.ATTRIBUTES));
        i.setItem(0, button(Material.PAPER, Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK), "char"));
        createAttributePointsButton(i, character);
        i.setItem(8, button(Material.GLOWSTONE_DUST, Rpg.get().getLocalizationService().translate(LocalizationKeys.CONFIRM), "char attributes tx-commit"));
        Map<String, Integer> transientAttributes = character.getTransientAttributes();
        Map<String, Integer> attributes = character.getCharacterBase().getAttributes();
        Map<String, Integer> attributesTransaction = character.getAttributesTransaction();

        PropertyService propertyService = Rpg.get().getPropertyService();

        Map<String, AttributeConfig> ac = propertyService.getAttributes();
        int k = 0;
        for (Map.Entry<String, AttributeConfig> a : ac.entrySet()) {

            AttributeConfig aconf = a.getValue();
            int transientVal = transientAttributes.get(a.getKey());
            int real = attributes.get(a.getKey());
            int tx = attributesTransaction.get(a.getKey());

            int slot = attributButtonSlots[k];
            if (character.getAttributePoints() > 0) {
                ItemStack attrInc = button(Resourcepack.PLUS, ChatColor.GREEN + "+",
                        "char attribute-add " + aconf.getId() + " true " + slot);

                i.setItem(slot - 9, attrInc);
            }
            ItemStack atris = unclickableIcon(charAttributeToItemStack(aconf, real, transientVal, tx));


            i.setItem(slot, atris);
            k++;
        }
        return i;
    }

    private static void createAttributePointsButton(Inventory inventory, IActiveCharacter character) {
        if (character.getAttributePoints() > 0) {
            ItemStack itemStack = unclickableInterface(Material.BOOK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.ATTRIBUTE_POINTS);
            itemMeta.setDisplayName(ChatColor.GREEN + translate + ChatColor.RESET + " " + character.getAttributePoints());
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(1, itemStack);
        }
    }

    private static ItemStack charAttributeToItemStack(AttributeConfig a, int base, int tr, int inTx) {
        base += inTx;

        ItemStack itemStack = new ItemStack(Material.matchMaterial(a.getItemType()));

        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = a.getName();
        itemMeta.setDisplayName(ChatColor.GREEN + name);

        List<String> lore = new ArrayList<>();
        if (a.getDescription() != null) {
            lore.add(a.getDescription());
        }

        lore.add(ChatColor.GRAY + "-------------------");
        lore.add("");
        lore.add(ChatColor.WHITE + "Effective Value: " + base + tr);
        lore.add(ChatColor.ITALIC.toString() + ChatColor.YELLOW + "Char. Value: " + base);
        lore.add(ChatColor.GRAY + "-------------------");
        lore.add("");

        Map<Integer, Float> propBonus = a.getPropBonus();
        if (!propBonus.isEmpty()) {
            lore.add("");
            PropertyService propertyService = Rpg.get().getPropertyService();
            for (Map.Entry<Integer, Float> e : propBonus.entrySet()) {
                String nameById = propertyService.getNameById(e.getKey());
                Float value = e.getValue();
                lore.add(" " + ChatColor.WHITE + nameById.replaceAll("_", " ") + " " + formatPropertyValue(value));
            }
        }


        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setCustomModelData(1002);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private static String formatPropertyValue(Float value) {
        float v = value.floatValue();
        if (v > 0) {
            return ChatColor.GREEN + "+" + v;
        }
        return ChatColor.RED + "-" + v;
    }


    public static Inventory getCharacterAllowedArmor(ISpigotCharacter character, int page) {
        String name = "char_allowed_items_armor" + character.getName();
        Inventory inventory = CACHED_MENUS.get(name);
        if (inventory == null) {
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_view");
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

    public static Inventory getCharacterAllowedWeapons(ISpigotCharacter character, int page) {
        String name = "char_allowed_items_weapons" + character.getName();
        Inventory inventory = CACHED_MENUS.get(name);
        if (inventory == null) {
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_view");
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
        Material material = Material.matchMaterial(key.getId());
        List<String> lore = new ArrayList<>();
        ItemStack is = new ItemStack(material);

        LocalizationService localizationService = Rpg.get().getLocalizationService();
        String translate = localizationService.translate(LocalizationKeys.ITEM_CLASS);
        lore.add(ChatColor.GRAY + translate + ": " + ChatColor.GREEN + key.getItemClass().getName());
        if (damage > 0) {
            translate = localizationService.translate(LocalizationKeys.ITEM_DAMAGE);
            lore.add(ChatColor.GRAY + translate + ": " + ChatColor.RED + damage);
        }
        if (Rpg.get().getPluginConfig().DEBUG.isBalance() && key.getModelId() != null && !key.getModelId().isEmpty()) {
            lore.add(ChatColor.DARK_GRAY + "DEBUG:: CustomModelData:" + key.getModelId());
        }

        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setCustomModelData(Integer.valueOf(key.getModelId()));
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        unclickableInterface(is);

        return is;
    }
}
