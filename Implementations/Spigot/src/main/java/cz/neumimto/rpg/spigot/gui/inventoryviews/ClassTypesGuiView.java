package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class ClassTypesGuiView extends ConfigurableInventoryGui {

    private static ChestGui cachedGui;

    public ClassTypesGuiView() {
        super("ClassTypes.conf");
    }

    @Override
    public void clearCache() {
        super.clearCache();
        initialize();
    }

    @Override
    public void initialize() {
        cachedGui = loadGui();
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender) {
        Map<String, List<GuiCommand>> map = new HashMap<>();
        List<GuiCommand> guiCommands = new ArrayList<>();

        Map<String, ClassTypeDefinition> cTypes = Rpg.get().getPluginConfig().CLASS_TYPES;

        for (Map.Entry<String, ClassTypeDefinition> e : cTypes.entrySet()) {
            String primaryColor = e.getValue().getPrimaryColor();
            ChatColor c = ChatColor.valueOf(primaryColor.toUpperCase());
            guiCommands.add(new GuiCommand(i(Material.CRAFTING_TABLE, e.getValue().getModelId(), c, e.getKey()), "ninfo classes " + e.getKey()));
        }

        map.put("ClassType", guiCommands);
        return map;
    }

    public static ChestGui get() {
        return cachedGui;
    }

}
