package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class ClassViewGui extends ConfigurableInventoryGui {

    private static Map<String, ChestGui> classes = new HashMap<>();

    private static ClassViewGui instance;

    @Inject
    private ClassService classService;

    public ClassViewGui() {
        super("Class.conf");
    }

    @Override
    public void clearCache() {
        super.clearCache();
        classes.clear();
        classService.getClassDefinitions()
                .forEach(a -> {
                    ChestGui chestGui = loadGui(null, a.getName());
                    classes.put(a.getName().toLowerCase(Locale.ROOT), chestGui);
                });
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender, String param) {
        return Collections.emptyMap();
    }

    @Override
    public void initialize() {
        clearCache();
    }

    @Override
    protected String getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        ClassDefinition cd = classService.getClassDefinitionByName(param);
        return getPrefix(guiConfig) + ChatColor.valueOf(cd.getPreferedColor()) + cd.getName();
    }

    public static ChestGui get(String className) {
        return classes.get(className.toLowerCase(Locale.ROOT));
    }

}
