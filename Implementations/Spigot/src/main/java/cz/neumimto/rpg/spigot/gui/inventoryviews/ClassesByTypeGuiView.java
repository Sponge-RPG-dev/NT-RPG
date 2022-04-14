package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class ClassesByTypeGuiView extends ConfigurableInventoryGui {

    private static Map<String, ChestGui> types = new HashMap<>();
    private static ClassesByTypeGuiView instance;

    @Inject
    private ClassService classService;

    public ClassesByTypeGuiView() {
        super("ClassesByType.conf");
        instance = this;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        types.clear();
        for (String ct : Rpg.get().getPluginConfig().CLASS_TYPES.keySet()) {
            types.put(ct, instance.loadGui(null, ct));
        }
    }

    @Override
    public void initialize() {
        clearCache();
    }


    public static ChestGui get(String type) {
        return types.get(type);
    }

    @Override
    protected Component getTitle(CommandSender commandSender, GuiConfig guiConfig, String param) {
        return getPrefix(guiConfig).append(Component.text(param));
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender, String type) {
        Map<String, List<GuiCommand>> map = new HashMap<>();
        List<GuiCommand> list = new ArrayList<>();
        classService.getClassDefinitions().stream()
                .filter(a -> a.getClassType().equalsIgnoreCase(type))
                .forEach(a -> {
                    ItemStack is = CharacterGuiView.classDefinitionToItemStack(a);
                    list.add(new GuiCommand(is, "ninfo class " + a.getName()));
                });
        map.put("Class", list);
        return map;
    }

}
