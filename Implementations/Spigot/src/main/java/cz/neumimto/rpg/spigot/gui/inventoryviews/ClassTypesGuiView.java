package cz.neumimto.rpg.spigot.gui.inventoryviews;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.common.gui.ConfigInventory;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ClassTypesGuiView extends GuiHelper {

    public static Inventory create() {
        Map<String, ClassTypeDefinition> cTypes = api.getPluginConfig().CLASS_TYPES;
        Object[] context = new Object[]{
                cTypes,
                (Supplier<T[]>) () -> cTypes.entrySet().stream()
                        .map(this::classTypeButton)
                        .collect(Collectors.toList())
                        .toArray(initArray(cTypes.size()))
        };
        ConfigInventory c = createCachedMenu(
                sFactorz, guiName, gui, context
        );
        Inventory i = createInventory(null, "gui.label.class-types");
        c.fill(i);
    }
}
