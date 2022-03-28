package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.items.ClassItem;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

import static cz.neumimto.rpg.spigot.gui.inventoryviews.WeaponGuiView.toItemStack;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class ClassWeaponsGuiView extends ConfigurableInventoryGui {

    private static final Map<String, ChestGui> cache = new HashMap<>();

    private static ClassWeaponsGuiView instance;

    @Inject
    private ClassService classService;

    @Inject
    private LocalizationService localizationService;

    public ClassWeaponsGuiView() {
        super("ClassWeapons.conf");
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
        for (ClassDefinition cd : classService.getClasses().values()) {
            cache.put(cd.getName(), instance.loadGui(null, cd.getName()));
        }
    }

    @Override
    protected String getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(param);
        return getPrefix(guiConfig) + ChatColor.valueOf(classDefinitionByName.getPreferedColor()) + classDefinitionByName.getName() + " " + localizationService.translate(LocalizationKeys.WEAPONS);
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender, String className) {
        Map<String, List<GuiCommand>> map = new HashMap<>();
        List<GuiCommand> list = new ArrayList<>();

        ClassDefinition classDefinitionByName = classService.getClassDefinitionByName(className);
        Set<ClassItem> weapons = classDefinitionByName.getWeapons();

        for (ClassItem weapon : weapons) {
            ItemStack itemStack = toItemStack(weapon.getType(), weapon.getDamage());
            list.add(new GuiCommand(itemStack));
        }

        map.put("Weapon", list);
        return map;
    }

}
