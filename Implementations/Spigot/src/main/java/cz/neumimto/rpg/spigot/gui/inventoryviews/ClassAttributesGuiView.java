package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.spigot.gui.ItemLoreFactory.header;
import static cz.neumimto.rpg.spigot.gui.ItemLoreFactory.line;
import static cz.neumimto.rpg.spigot.gui.SpigotGuiHelper.formatPropertyValue;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class ClassAttributesGuiView extends ConfigurableInventoryGui {

    private static final Map<String, ChestGui> cache = new HashMap<>();

    private static ClassAttributesGuiView instance;

    @Inject
    private ClassService classService;

    @Inject
    private LocalizationService localizationService;

    public ClassAttributesGuiView() {
        super("ClassAttributes.conf");
        instance = this;
    }

    public static ChestGui get(String className) {
        if (cache.containsKey(className)) {
            return cache.get(className);
        }
        ChestGui gui = instance.loadGui(null, className);
        cache.put(className, gui);
        return gui;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        cache.clear();
    }

    @Override
    protected String getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(param);
        return getPrefix(guiConfig) + ChatColor.valueOf(classDefinitionByName.getPreferedColor()) + classDefinitionByName.getName() + " " + localizationService.translate(LocalizationKeys.ATTRIBUTES);
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender, String className) {
        Map<String, List<GuiCommand>> map = new HashMap<>();

        ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(className);
        Map<AttributeConfig, Integer> startingAttributes = classDefinitionByName.getStartingAttributes();

        List<GuiCommand> list = toList(startingAttributes);

        map.put("Attributes", list);
        return map;
    }

    private List<GuiCommand> toList(Map<AttributeConfig, Integer> a) {
        return a.entrySet().stream()
                .filter(w -> !w.getKey().isHidden())
                .sorted((o1, o2) -> o1.getKey().getName().compareToIgnoreCase(o2.getKey().getName()))
                .map(w -> attributeConfigToItemStack(w.getKey(), w.getValue()))
                .map(GuiCommand::new)
                .collect(Collectors.toList());
    }

    public static ItemStack attributeConfigToItemStack(AttributeConfig a, Integer value) {
        ItemStack itemStack = DatapackManager.instance.findById(a.getItemType(), a.getModel());

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> list = new ArrayList<>();
        list.add(header(net.md_5.bungee.api.ChatColor.of(a.getHexColor()) + a.getName()));
        list.add(line(net.md_5.bungee.api.ChatColor.GOLD.toString() + net.md_5.bungee.api.ChatColor.ITALIC + a.getDescription()));

        Map<Integer, Float> propBonus = a.getPropBonus();
        if (!propBonus.isEmpty()) {
            list.add(line(""));
            PropertyService propertyService = Rpg.get().getPropertyService();
            for (Map.Entry<Integer, Float> e : propBonus.entrySet()) {
                String nameById = propertyService.getNameById(e.getKey());
                Float valuee = e.getValue();
                list.add(line(" " + net.md_5.bungee.api.ChatColor.WHITE + nameById.replaceAll("_", " ") + " " + formatPropertyValue(valuee)));
            }
        }

        itemMeta.setLore(list);
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName("");
        if (a.getModel() > 0) {
            itemMeta.setCustomModelData(value);
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}

