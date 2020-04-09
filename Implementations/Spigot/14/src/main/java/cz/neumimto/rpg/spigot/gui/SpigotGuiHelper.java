package cz.neumimto.rpg.spigot.gui;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
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
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.items.SpigotRpgItemType;
import cz.neumimto.rpg.spigot.skills.SpigotSkillService;
import cz.neumimto.rpg.spigot.skills.SpigotSkillTreeInterfaceModel;
import de.tr7zw.nbtapi.NBTItem;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
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

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpigotGuiHelper {

    static final int[] inventoryIds;
    private static final int[] attributButtonSlots;

    public static ItemLoreFactory itemLoreFactory;

    private static Map<String, ConfigInventory> CACHED_MENUS = new HashMap<>();

    static {
        itemLoreFactory = new ItemLoreFactory();
        List<Integer> w = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j < 6; j++) {
                w.add(j * 9 + i);
            }
        }

        inventoryIds = w.stream().mapToInt(i -> i).toArray();
        attributButtonSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 36, 37, 38, 39, 40, 41, 42, 43, 45};

    }

    public static void initInventories() {
        Config config = ConfigFactory.load(SpigotGuiHelper.class.getClassLoader(), "guis.conf");
        RpgApi api = Rpg.get();

        NashornScriptEngineFactory sFactorz = new NashornScriptEngineFactory();

        for (Config gui : config.getConfigList("gui")) {
            String guiName = gui.getString("type");


            switch (guiName) {
                case "class_template":
                    Collection<ClassDefinition> values = api.getClassService().getClasses().values();
                    for (ClassDefinition context : values) {
                        ConfigInventory c = createCachedMenu(sFactorz, guiName, gui, context);
                        CACHED_MENUS.put(guiName + context.getName(), c);
                    }
                    break;
                case "class_types":
                    Map<String, ClassTypeDefinition> cTypes = api.getPluginConfig().CLASS_TYPES;
                    Object[] context = new Object[]{
                            cTypes,
                            (Supplier<ItemStack[]>) () -> cTypes.entrySet().stream()
                                    .map(SpigotGuiHelper::classTypeButton)
                                    .collect(Collectors.toList())
                                    .toArray(new ItemStack[cTypes.size() - 1])
                    };
                    ConfigInventory c = createCachedMenu(
                            sFactorz, guiName, gui, context
                    );

                    CACHED_MENUS.put(guiName, c);
                    break;
                case "classes_by_type":

                    Set<String> types = api.getClassService().getClassDefinitions().stream()
                            .map(ClassDefinition::getClassType)
                            .collect(Collectors.toSet());

                    for (String type : types) {
                        Object[] context2 = new Object[]{
                                type,
                                (Supplier<ItemStack[]>) () -> api.getClassService().getClassDefinitions()
                                .stream().filter(a->a.getClassType().equals(type)).map(a ->toItemStack(a, ""))
                                        .collect(Collectors.toList())
                                        .toArray(new ItemStack[types.size() - 1])
                        };
                        ConfigInventory c2 = createCachedMenu(
                                sFactorz, guiName, gui, context2
                        );

                        CACHED_MENUS.put(guiName + type, c2);
                    }
                    break;
            }

        }
    }

    private static ConfigInventory createCachedMenu(NashornScriptEngineFactory factory,
                                                    String guiName,
                                                    Config gui,
                                                    Object context) {
        List<String> inv = gui.getStringList("inv");
        List<String> items = gui.getStringList("items");
        String conditions = gui.getString("conditions");
        String dynamicSpace = gui.getString("dynamic_space");
        String command = gui.getString("command");
        String commandFn = "function command(context_, command_) { %cmd%; }";
        String condFn = "function validate(context_, slot_) { %cond% ; return true}";

        ScriptEngine scriptEngine = factory.getScriptEngine();
        try {
            scriptEngine.eval(commandFn.replaceAll("%cmd%", command));
            scriptEngine.eval(condFn.replaceAll("%cond%", conditions));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable i = (Invocable) scriptEngine;
        return createCachedMenu(i, guiName, inv, dynamicSpace, items, context);
    }

    private static ConfigInventory createCachedMenu(Invocable i,
                                                    String guiName,
                                                    List<String> inv,
                                                    String dynamicSpace,
                                                    List<String> items,
                                                    Object context
    ) {


        Map<Character, ItemStack> itemMap = new HashMap<>();
        List<ItemStack> invContent = new ArrayList<>();
        ItemStack blank = parseItemsAndReturnBlank(i, context, itemMap, items);

        try {
            prepareInventory(inv, i, invContent, blank, context, guiName, itemMap);
        } catch (ScriptException | NoSuchMethodException e) {
            Log.error("Could not parse inventory from gui.conf " + guiName);
            e.printStackTrace();
        }
        ItemStack[] staticContent = invContent.toArray(new ItemStack[invContent.size() - 1]);
        if (!(!dynamicSpace.isEmpty() && Object[].class.isAssignableFrom(context.getClass()))) {
            return ConfigInventory.of(staticContent);
        } else {
            Object[] c = (Object[]) context;
            return ConfigInventory.of(staticContent, blank, ((Supplier<ItemStack[]>) c[1]).get());
        }

    }

    private static void prepareInventory(List<String> inv,
                                         Invocable i,
                                         List<ItemStack> invContent,
                                         ItemStack blank,
                                         Object context,
                                         String guiName,
                                         Map<Character, ItemStack> itemMap)
            throws ScriptException, NoSuchMethodException {

        for (String s : inv) {
            char[] chars = s.toCharArray();
            for (char c : chars) {
                boolean display = (boolean) i.invokeFunction("validate", context, c);
                if (display) {
                    ItemStack itemStack = itemMap.get(c);
                    if (itemStack == null) {
                        throw new IllegalStateException("Gui " + guiName + " missing item " + c);
                    }
                    invContent.add(itemStack);
                } else {
                    invContent.add(blank.clone());
                }
            }
        }

    }

    private static ItemStack parseItemsAndReturnBlank(Invocable i, Object context, Map<Character, ItemStack> itemMap, List<String> items) {
        ItemStack blank = null;
        for (String item : items) {
            String[] split = item.split(",");
            String command = split[4];

            ItemStack itemStack = itemStringToItemStack(split, () -> {
                try {
                    return (String) i.invokeFunction("command", context, command);
                } catch (ScriptException | NoSuchMethodException e) {
                    return "";
                }
            });
            if (command.equals("---")) {
                blank = itemStack;
            }
            itemMap.put(split[0].charAt(0), itemStack);
        }
        return blank;
    }

    private static ItemStack itemStringToItemStack(String[] split, Supplier<String> command) {
        Material material = Material.matchMaterial(split[2]);
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Rpg.get().getLocalizationService().translate(split[1]));
        int i1 = Integer.parseInt(split[3]);
        if (i1 > 0) {
            itemMeta.setCustomModelData(i1);
        }

        itemStack.setItemMeta(itemMeta);
        String cmd = command.get();
        if ("---".equalsIgnoreCase(cmd)) {
            itemStack = unclickableInterface(itemStack);
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setBoolean("ntrpg.item-iface", true);
            itemStack = nbtItem.getItem();
        } else {
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setString("ntrpg.item-command", cmd);
            itemStack = nbtItem.getItem();
        }
        return itemStack;
    }


    private static ItemStack classTypeButton(Map.Entry<String, ClassTypeDefinition> entry) {
        return button(Material.CRAFTING_TABLE,
                ChatColor.valueOf(entry.getValue().getPrimaryColor()) + entry.getKey(),
                "ninfo classes " + entry.getKey(), entry.getValue().getModelId());
    }

    public static Inventory createMenuInventoryClassTypesView(Player player) {
        Inventory i = createInventoryTemplate(player, "Classes");
        ConfigInventory staticInventory = CACHED_MENUS.get("class_types");
        staticInventory.fill(i);
        return i;
    }

    public static Inventory createMenuInventoryClassesByTypeView(Player player, String classType) {
        Inventory i = createInventoryTemplate(player, "Classes");
        ConfigInventory staticInventory = CACHED_MENUS.get("classes_by_type" + classType);
        staticInventory.fill(i);
        return i;
    }

    private static ItemStack toItemStack(ClassDefinition a, String backCommand) {
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

    private static ItemStack button(Material material, String name, String command, Integer data) {
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

    private static ItemStack unclickableInterface(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return unclickableIcon(itemStack);
    }

    private static ItemStack unclickableIcon(ItemStack itemStack) {
        NBTItem nbti = new NBTItem(itemStack);
        nbti.setBoolean("ntrpg.item-iface", true);
        return nbti.getItem();
    }

    private static ItemStack unclickableInterface(Material material, int model) {
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

    public static Inventory createClassInfoView(Player player, ClassDefinition cc, String back) {
        Inventory i = createInventoryTemplate(player, ChatColor.valueOf(cc.getPreferedColor()) + cc.getName());
        ConfigInventory staticInventory = CACHED_MENUS.get("class_template" + cc.getName());
        staticInventory.fill(i);
        return i;
    }

    public static Inventory createClassWeaponView(Player player, ClassDefinition cc, Set<ClassItem> weapons) {
        String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.WEAPONS);
        Inventory i = createInventoryTemplate(player, ChatColor.valueOf(cc.getPreferedColor()) + cc.getName() + ChatColor.RESET + " " + translate);
        i.setItem(0, button(Material.PAPER, Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK), "ninfo class " + cc.getName()));
        int w = 9;
        SpigotDamageService damageService = (SpigotDamageService) Rpg.get().getDamageService();
        String dmg = Rpg.get().getLocalizationService().translate(LocalizationKeys.ITEM_DAMAGE);
        if (weapons == null) {
            weapons = Collections.emptySet();
        }
        for (ClassItem weapon : weapons) {
            SpigotRpgItemType type = (SpigotRpgItemType) weapon.getType();
            ItemStack itemStack = new ItemStack(type.getMaterial());
            double damage = weapon.getDamage();

            if (damage <= 0) {
                damage = type.getDamage();
            }
            ChatColor colorByDamage = ChatColor.RED;//valueOf(damageService.getColorByDamage(damage));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (type.getModelId() != null) {
                itemMeta.setCustomModelData(Integer.parseInt(type.getModelId()));
            }
            List<String> list = new ArrayList<>();
            list.add(ChatColor.GRAY + dmg + ": " + colorByDamage + damage);
            itemMeta.setLore(list);
            itemStack.setItemMeta(itemMeta);

            i.setItem(w, unclickableInterface(itemStack));
            w++;
        }
        return i;
    }

    public static Inventory createArmorView(Player player, ClassDefinition cc, Set<ClassItem> armor) {
        String translate = Rpg.get().getLocalizationService().translate(LocalizationKeys.ARMOR);
        Inventory i = createInventoryTemplate(player, ChatColor.valueOf(cc.getPreferedColor()) + cc.getName() + " " + ChatColor.RESET + translate);

        createArmorView(i, armor, "ninfo class " + cc.getName());
        return i;
    }

    public static void createArmorView(Inventory i, Set<ClassItem> armor, String backCommand) {
        i.setItem(0, button(Material.PAPER, Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK), backCommand));
        int w = 9;
        String damageLabel = Rpg.get().getLocalizationService().translate(LocalizationKeys.ITEM_ARMOR);
        for (ClassItem ar : armor) {
            SpigotRpgItemType type = (SpigotRpgItemType) ar.getType();
            double damage = ar.getDamage();

            i.setItem(w, toItemStack(type, damage, damageLabel));
            w++;
        }
    }

    private static ItemStack toItemStack(SpigotRpgItemType type, double damage, String damageLabel) {
        ItemStack itemStack = new ItemStack(type.getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (type.getModelId() != null) {
            itemMeta.setCustomModelData(Integer.parseInt(type.getModelId()));
        }
        if (damage > 0) {
            List<String> lore = new ArrayList<>();
            SpigotDamageService spigotDamageService = (SpigotDamageService) Rpg.get().getDamageService();
            String colorByDamage = spigotDamageService.getColorByDamage(damage);
            lore.add(damageLabel + ":" + colorByDamage + damage);
            itemMeta.setLore(lore);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);

        return unclickableIcon(itemStack);
    }

    public static void fillWeaponView(Inventory i, Map<RpgItemType, Double> items, String backCommand) {
        i.setItem(0, button(Material.PAPER, Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK), backCommand));
        int w = 9;
        String damageLabel = Rpg.get().getLocalizationService().translate(LocalizationKeys.ITEM_DAMAGE);

        for (Map.Entry<RpgItemType, Double> entry : items.entrySet()) {
            SpigotRpgItemType weapon = (SpigotRpgItemType) entry.getKey();
            double damage = entry.getValue();

            i.setItem(w, toItemStack(weapon, damage, damageLabel));
            w++;
        }
    }

    public static void fillArmorView(Inventory i, Set<RpgItemType> allowedArmor, String backCommand) {
        i.setItem(0, button(Material.PAPER, Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK), backCommand));
        int w = 9;
        String damageLabel = Rpg.get().getLocalizationService().translate(LocalizationKeys.ITEM_DAMAGE);

        for (RpgItemType entry : allowedArmor) {
            double damage = entry.getArmor();

            i.setItem(w, toItemStack((SpigotRpgItemType) entry, damage, damageLabel));
            w++;
        }

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
        int y = skillTree.getCenter().value + skillTreeViewModel.getLocation().value; //y
        int x = skillTree.getCenter().key + skillTreeViewModel.getLocation().key; //x

        if (skillTreeMap == null) {
            throw new IllegalStateException("No AsciiMap defined for skilltree: " + skillTree.getId());
        }

        int columns = skillTreeMap[0].length;
        int rows = skillTreeMap.length;

        SpigotSkillTreeViewModel.InteractiveMode interactiveMode = skillTreeViewModel.getInteractiveMode();
        ItemStack md = interactiveModeToitemStack(character, interactiveMode);
        i.setItem(8, md);

        SpigotSkillService skillService = (SpigotSkillService) Rpg.get().getSkillService();

        int pointer = 0;

        for (int k = -3; k <= 4; k++) { //x
            for (int l = -3; l < 3; l++) { //y
                int slot = inventoryIds[pointer];
                pointer++;
                if (x + k >= 0 && x + k < rows) {
                    if (l + y >= 0 && l + y < columns) {

                        short id = skillTreeMap[x + k][l + y];
                        ItemStack itemStack = null;
                        if (id > 0) {
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
                        }

                        if (itemStack == null) {
                            itemStack = blank();
                        }
                        i.setItem(slot, itemStack);
                    } else {
                        i.setItem(slot, createSkillTreeInventoryMenuBoundary());
                    }
                } else {
                    i.setItem(slot, createSkillTreeInventoryMenuBoundary());
                }
            }
        }
        return i;
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
        itemMeta.setDisplayName(null);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("ntrpg.item-command", "skilltree skill " + skill.getId());
        return nbtItem.getItem();
    }


    private static ItemStack createSkillTreeInventoryMenuBoundary() {
        ItemStack is = unclickableInterface(Material.RED_STAINED_GLASS_PANE);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setCustomModelData(1235);
        is.setItemMeta(itemMeta);
        return is;
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

    public static Inventory createCharacterMenu(Player player, ISpigotCharacter cc) {
        Inventory i = createInventoryTemplate(player, cc.getName());
        if (!cc.getAllowedArmor().isEmpty()) {
            i.setItem(10, button(Material.DIAMOND_CHESTPLATE, Rpg.get().getLocalizationService().translate(LocalizationKeys.ARMOR), "char armor"));
        }
        if (!cc.getAllowedWeapons().isEmpty()) {
            i.setItem(11, button(Material.DIAMOND_SWORD, Rpg.get().getLocalizationService().translate(LocalizationKeys.WEAPONS), "char weapons"));
        }

        i.setItem(12, button(Material.BLAZE_POWDER, Rpg.get().getLocalizationService().translate(LocalizationKeys.ATTRIBUTES), "char attributes"));

        Map<String, PlayerClassData> classes = cc.getClasses();

        int s = 37;
        for (PlayerClassData value : classes.values()) {
            ClassDefinition classDefinition = value.getClassDefinition();
            ItemStack itemStack = toItemStack(classDefinition, "char");
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.valueOf(classDefinition.getPreferedColor()) + classDefinition.getName() + itemMeta.getDisplayName() + ChatColor.RESET + "| Lvl: " + ChatColor.GREEN + value.getLevel());
            itemStack.setItemMeta(itemMeta);
            i.setItem(s, itemStack);
            s++;
        }


        return i;
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


}
