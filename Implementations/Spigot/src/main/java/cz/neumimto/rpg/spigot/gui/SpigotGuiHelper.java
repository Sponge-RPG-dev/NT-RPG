package cz.neumimto.rpg.spigot.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.gui.SkillTreeViewModel;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.gui.ConfigInventory;
import cz.neumimto.rpg.common.gui.DynamicInventory;
import cz.neumimto.rpg.common.gui.TemplateInventory;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import cz.neumimto.rpg.spigot.gui.elements.Icon;
import cz.neumimto.rpg.spigot.skills.SpigotSkillService;
import cz.neumimto.rpg.spigot.skills.SpigotSkillTreeInterfaceModel;
import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

public class SpigotGuiHelper {

    static int[] inventoryIds;
    private static int[] attributButtonSlots;

    public static ItemLoreFactory itemLoreFactory;

    public static Map<String, Inventory> CACHED_MENUS = new HashMap<>();
    public static Map<String, ConfigInventory<ItemStack, Inventory>> CACHED_MENU_TEMPLATES = new HashMap<>();

    public static Map<EntityDamageEvent.DamageCause, ItemStack> damageTypeToItemStack = new HashMap<>();

    public static void initInventories() {
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

        damageTypeToItemStack.put(ENTITY_ATTACK, unclickableIcon(Material.STONE_SWORD, 354,"ENTITY_ATTACK"));
        damageTypeToItemStack.put(ENTITY_SWEEP_ATTACK, unclickableIcon(Material.STONE_SWORD, 354,"ENTITY_SWEEP_ATTACK"));
        damageTypeToItemStack.put(CONTACT, unclickableIcon(Material.CACTUS, 354,"CONTACT"));
        damageTypeToItemStack.put(CUSTOM, unclickableIcon(Material.BARRIER, 354,"CUSTOM"));
        damageTypeToItemStack.put(DROWNING, unclickableIcon(Material.WATER, 354,"DROWNING"));
        damageTypeToItemStack.put(ENTITY_EXPLOSION, unclickableIcon(Material.TNT, 354,"ENTITY_EXPLOSION"));
        damageTypeToItemStack.put(BLOCK_EXPLOSION, unclickableIcon(Material.TNT, 354,"BLOCK_EXPLOSION"));
        damageTypeToItemStack.put(FALL, unclickableIcon(Material.IRON_BOOTS, 354,"FALL"));
        damageTypeToItemStack.put(FIRE, unclickableIcon(Material.BLAZE_POWDER, 354,"FIRE"));
        damageTypeToItemStack.put(STARVATION, unclickableIcon(Material.ROTTEN_FLESH, 354,"STARVATION"));
        damageTypeToItemStack.put(LAVA, unclickableIcon(Material.LAVA, 354,"LAVA"));
        damageTypeToItemStack.put(PROJECTILE, unclickableIcon(Material.TIPPED_ARROW, 354,"PROJECTILE"));
        damageTypeToItemStack.put(VOID, unclickableIcon(Material.NETHER_PORTAL, 354,"VOID"));
        damageTypeToItemStack.put(MAGIC, unclickableIcon(Material.ENCHANTED_BOOK, 354,"MAGIC"));
        damageTypeToItemStack.put(LIGHTNING, unclickableIcon(Material.NETHER_STAR, 354,"LIGHTNING"));

        CACHED_MENU_TEMPLATES.clear();
        CACHED_MENUS.clear();
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
                    .map(a -> toSpellbookItemStack(a, "char"))
                    .collect(Collectors.toList())
                    .toArray(new ItemStack[cc.getClasses().size() == 0 ? 0 : cc.getClasses().size() - 1]);
            DynamicInventory inv = dView.setActualContent(chars);
            dynamicInventory = createInventoryTemplate(cc.getName());
            inv.fill(dynamicInventory);
            CACHED_MENUS.put(name, dynamicInventory);
        }
        return dynamicInventory;
    }

    public static ItemStack toSpellbookItemStack(ClassDefinition a, String backCommand) {
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


    public static ItemStack button(Material material, String name, String command) {
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

    public static ItemStack item(Material material, String nameKey, Integer data) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setDisplayName(Rpg.get().getLocalizationService().translate(nameKey));
        if (data != null) {
            itemMeta.setCustomModelData(data);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
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

    public static ItemStack unclickableIcon(ItemStack itemStack, String tag) {
        NBTItem nbti = new NBTItem(itemStack);
        nbti.setBoolean("ntrpg.item-iface", true);
        nbti.setBoolean(tag, true);
        return nbti.getItem();
    }

    public static ItemStack unclickableIcon(Material itemStack, int model, String name) {
        ItemStack itemStack1 = new ItemStack(itemStack);
        NBTItem nbti = new NBTItem(itemStack1);
        nbti.setBoolean("ntrpg.item-iface", true);
        nbti.setString("display", name);
        return nbti.getItem();
    }

    public static ItemStack unclickableInterface(Material material, int model) {
        ItemStack itemStack = new ItemStack(material);
        return unclickableInterface(itemStack, model);
    }

    private static ItemStack unclickableInterface(ItemStack itemStack, int model) {
        setUnclickableInterfaceItemMeta(itemStack, model);
        return unclickableIcon(itemStack);
    }

    private static void setUnclickableInterfaceItemMeta(ItemStack itemStack, int model) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setCustomModelData(model);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemStack unclickableInterface(Material material, int model, String tag) {
        return unclickableInterface(new ItemStack(material), model, tag);
    }

    public static ItemStack unclickableInterface(ItemStack itemStack, int model, String tag) {
        setUnclickableInterfaceItemMeta(itemStack, model);
        return unclickableIcon(itemStack, tag);
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
        i.setItem(17, blank());
        i.setItem(26, button(Resourcepack.UP, "Up", "skilltree north"));
        i.setItem(35, button(Resourcepack.DOWN, "Down", "skilltree south"));
        i.setItem(44, button(Resourcepack.RIGHT, "Right", "skilltree west"));
        i.setItem(53, button(Resourcepack.LEFT, "Left", "skilltree east"));
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

            if (slotId % 9 == 0) {
                y++;
                x = -4;
            } else {
                x++;
            }
            int realX = centerX + y;
            int realY = centerY + x;

            if (isInRange(skillTreeMap, realX, realY) && realX < rows && realY < columns) {

                short id = skillTreeMap[realX][realY];
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
            lore = itemLoreFactory.toLore(character, skillData, nameColor);
            model.addToCache(skill, lore);
        } else {
            lore = fromCache;
        }

        Material material = getSkillIcon(skillData);
        ItemStack itemStack = createSkillIconItemStack(material, skillData, lore);
        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setString("ntrpg.item-command", "skilltree skill " + skillData.getSkillName());
        return nbtItem.getItem();
    }

    private static ItemStack createSkillIconItemStack(Material material, SkillData skillData, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.values());
        if (skillData.getModelId() != null) {
            itemMeta.setCustomModelData(skillData.getModelId());
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static Material getSkillIcon(SkillData skillData) {
        if (skillData.getIcon() != null) {
            Material mat = Material.matchMaterial(skillData.getIcon());
            if (mat != null) {
                return mat;
            }
        }
        return Material.STONE;
    }


    public static ItemStack toSpellbookItemStack(ISpigotCharacter character, PlayerSkillContext skillContext) {
        SkillData skillData = skillContext.getSkillData();
        List<String> lore = itemLoreFactory.toLore(character, skillData, ChatColor.GREEN);

        Material material = getSkillIcon(skillData);
        ItemStack itemStack = createSkillIconItemStack(material, skillData, lore);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("ntrpg.spellbook.learnedspell", skillData.getSkillName());
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
        int tx = character.getAttributesTransaction().get(id);
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
        List<String> lore = itemLoreFactory.toLore(a, base, tr);

        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName(" ");
        itemMeta.setCustomModelData(1002);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static String formatPropertyValue(Float value) {
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
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_allowed_items");
            Set<RpgItemType> allowedWeapons = character.getAllowedArmor();
            List<ItemStack> content = new ArrayList<>();
            for (RpgItemType ent : allowedWeapons) {
                ItemStack is = toSpellbookItemStack(ent, 0);
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
            TemplateInventory<ItemStack, Inventory> dView = (TemplateInventory<ItemStack, Inventory>) CACHED_MENU_TEMPLATES.get("char_allowed_items");
            Map<RpgItemType, Double> allowedWeapons = character.getAllowedWeapons();
            List<ItemStack> content = new ArrayList<>();
            for (Map.Entry<RpgItemType, Double> ent : allowedWeapons.entrySet()) {
                RpgItemType key = ent.getKey();
                Double value = ent.getValue();
                ItemStack is = toSpellbookItemStack(key, value);
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

    private static ItemStack toSpellbookItemStack(RpgItemType key, double damage) {
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
        if (key.getModelId() != null) {
            itemMeta.setCustomModelData(Integer.valueOf(key.getModelId()));
        }
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        return unclickableInterface(is);

    }

    public static Inventory createSpellbookInventory(ISpigotCharacter character) {
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        Inventory i = createInventoryTemplate(localizationService.translate("gui.spellbook.label"));
        CharacterBase characterBase = character.getCharacterBase();

        Map<String, PlayerSkillContext> skillsByName = character.getSkillsByName();
        Map<String, PlayerSkillContext> sorted = new TreeMap<>(Comparator.naturalOrder());
        sorted.putAll(skillsByName);

        String[][] rows = characterBase.getSpellbookPages();

        if (rows == null) {
            for (int j = 27; j < 54; j++) {
                i.setItem(j, createEmptySlot());
            }
        } else {
            int q = 27;
            for (String[] row : rows) {
                for (String s : row) {
                    if ("-".equals(s)) {
                        i.setItem(q, createEmptySlot());
                    } else {
                        PlayerSkillContext skill = character.getSkill(s);
                        if (skill == null) {
                            i.setItem(q, createEmptySlot());
                        } else {
                            i.setItem(q, toSpellbookItemStack(character, skill));
                        }
                    }
                    q++;
                }
            }
        }
        SpellbookListener.addInventory(character, i, sorted);
        return i;
    }

    public static ItemStack createEmptySlot() {
        return SpigotGuiHelper.unclickableInterface(Material.WHITE_STAINED_GLASS_PANE, 12345, "ntrpg.skillbook.emptyslot");
    }

    public static void createSkillDetailInventoryView(ISpigotCharacter character, SkillTree tree, SkillData skillData) {
        String back = Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK);
        ChestGui gui = new ChestGui(6, skillData.getSkillName());
        StaticPane background = new StaticPane(0, 0, 9, 6);
        gui.addPane(background);

        Player player = character.getPlayer();

        ItemStack backButton = item(Material.PAPER, back,12345);
        SpigotSkillTreeViewModel model = character.getLastTimeInvokedSkillTreeView();
        background.addItem(new GuiCommand(
                backButton,
                "skilltree view " + model.getViewedClass().getName(),
                player),0,0);

        if (skillData instanceof SkillPathData) {

            SkillPathData data = (SkillPathData) skillData;

            ItemStack of = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = of.getItemMeta();
            itemMeta.setDisplayName("Tier " + data.getTier());
            background.addItem(new Icon(of), 1,1);

            SkillService skillService = Rpg.get().getSkillService();

            int x = 1;
            int y = 1;
            for (Map.Entry<String, Integer> entry : data.getSkillBonus().entrySet()) {
                ISkill skill = skillService.getById(entry.getKey()).orElse(null);
                if (skill != null) {
                    ItemStack itemStack = skillToItemStack(character, character.getSkill(skill.getId()).getSkillData(), tree, model);
                    ItemMeta itemMeta1 = itemStack.getItemMeta();
                    itemMeta1.setDisplayName((entry.getValue() < 0 ? ChatColor.RED : ChatColor.DARK_GREEN)
                            + String.format("%+d", entry.getValue()) + " | " + entry.getKey());
                    itemStack.setItemMeta(itemMeta1);

                    background.addItem(new Icon(itemStack),x,y);
                    x++;
                    if (x % 8 == 0) {
                        x = 1;
                        y++;
                    }
                }
            }

        } else {
            String type = skillData.getSkill().getDamageType();
            if (type != null) {
                background.addItem(new Icon(damageTypeToItemStack(EntityDamageEvent.DamageCause.valueOf(type))),1,1);
            }

            int x = 1;
            int y = 3;

            List<ItemStack> itemStacks = configurationToItemStacks(skillData);
            for (ItemStack itemStack : itemStacks) {
                background.addItem(new Icon(itemStack),x,y);
                x++;
                if (x % 8 == 0) {
                    x = 1;
                    y++;
                }
            }

        }

        background.fillWith(blank(), e -> e.setCancelled(true));
        gui.show(player);
    }

    private static ItemStack damageTypeToItemStack(EntityDamageEvent.DamageCause type) {
        if (type == null) {
            return unclickableInterface(Material.STONE);
        }
        return damageTypeToItemStack.get(type);
    }

    private static List<ItemStack> configurationToItemStacks(SkillData skillData) {
        List<ItemStack> a = new ArrayList<>();
        Map<String, ItemString> skill_settings_icons = Rpg.get().getPluginConfig().SKILL_SETTINGS_ICONS;

        if (skillData.getSkillSettings() != null) {
            Map<String, String> nodes = skillData.getSkillSettings().getNodes();
            for (Map.Entry<String, String> s : nodes.entrySet()) {
                String s1 = configNodeToReadableString(s.getKey());
                String init = s.getValue();

                ItemString itemString = skill_settings_icons.get(s.getKey());
                ItemStack of = null;
                Material material;
                int variant = 0;
                String displayName = s1;
                if (itemString != null) {
                    material = Material.matchMaterial(itemString.itemId);
                    variant = Integer.parseInt(itemString.variant);
                } else {
                    material = Material.PAPER;
                    variant = 99;
                }
                of = new ItemStack(material);
                ItemMeta itemMeta = of.getItemMeta();
                itemMeta.setDisplayName(displayName);
                itemMeta.setCustomModelData(variant);
                itemMeta.setLore(Collections.singletonList(init));

                of.setItemMeta(itemMeta);
                a.add(of);
            }
        }
        return a;
    }

    private static String configNodeToReadableString(String t) {
        String a = t.replaceAll("_", " ");
        a = a.substring(0, 1).toUpperCase() + a.substring(1);
        return a;
    }
}
