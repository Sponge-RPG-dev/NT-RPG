package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import cz.neumimto.rpg.spigot.gui.elements.Icon;
import cz.neumimto.rpg.spigot.gui.elements.MaskPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassTypesGuiView extends GuiHelper {

    private static ChestGui cachedGui;

    public static ChestGui create() {

        if (cachedGui == null) {

            Map<String, ClassTypeDefinition> cTypes = Rpg.get().getPluginConfig().CLASS_TYPES;

            ChestGui gui = new ChestGui(6, t("gui.label.class-types"));

            MaskPane maskPane = new MaskPane(0,0,
                    new MaskPane.ItemMask(
                            "UUUUUUUUU",
                            "U-------U",
                            "U-------U",
                            "U-------U",
                            "U-------U",
                            "UUUUUUUUU"
                    ));

            maskPane.bindItem('U', new Icon(i(Material.WHITE_STAINED_GLASS_PANE, 12345)));
            maskPane.bindItem('-', new GuiItem(i(Material.GRAY_STAINED_GLASS_PANE, 12345)));
            maskPane.setDynamicContentMask('-');


            List<GuiItem> list = new ArrayList<>();
            for (Map.Entry<String, ClassTypeDefinition> e : cTypes.entrySet()) {
                String primaryColor = e.getValue().getPrimaryColor();
                ChatColor c = ChatColor.valueOf(primaryColor.toUpperCase());
                list.add(new GuiCommand(i(Material.CRAFTING_TABLE, e.getValue().getModelId(),c,e.getKey()), "ninfo classes " + e.getKey()));
            }

            maskPane.setDynamicContent(list);
            gui.addPane(maskPane);
            cachedGui = gui;
        }
        return cachedGui;
    }

}
