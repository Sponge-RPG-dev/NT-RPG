package cz.neumimto.rpg.spigot.gui;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.function.Consumer;

public class SpigotGuiHelper {

    public static StaticPane createMenuInventoryClassTypesView() {
        StaticPane pane = new StaticPane( 9, 6);
        makeBorder(pane, Material.LIGHT_GRAY_STAINED_GLASS);

        Map<String, ClassTypeDefinition> class_types = Rpg.get().getPluginConfig().CLASS_TYPES;

        int i = 1;
        int m = 1;
        for (Map.Entry<String, ClassTypeDefinition> entry : class_types.entrySet()) {
            ItemStack itemStack = new ItemStack(Material.CRAFTING_TABLE);
            pane.addItem(new GuiItem(itemStack, event -> {
                dispatchCommand(event, "ninfo class " + entry.getKey());
            }), i, m);
        }

        return pane;
    }

    public static void dispatchCommand(InventoryClickEvent event, String command) {
        HumanEntity whoClicked = event.getWhoClicked();
        Bukkit.dispatchCommand(whoClicked, command);
    }

    private static void makeBorder(StaticPane pane, Material mat) {
        int h = pane.getHeight();
        int l = pane.getLength();
        for (int i = 0; i < l; i++) {
            ItemStack itemStack = new ItemStack(mat);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("");
            itemStack.setItemMeta(itemMeta);
            pane.addItem(new GuiItem(itemStack, DENY_INTERACTION), 0, i);
            pane.addItem(new GuiItem(itemStack, DENY_INTERACTION), 0, l);
        }

        for (int i = 1; i < h - 1; i++) {
            ItemStack itemStack = new ItemStack(mat);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("");
            itemStack.setItemMeta(itemMeta);
            pane.addItem(new GuiItem(itemStack, DENY_INTERACTION), i, 0);
            pane.addItem(new GuiItem(itemStack, DENY_INTERACTION), h, 0);
        }
    }

    private static final Consumer<InventoryClickEvent> DENY_INTERACTION = event -> event.setCancelled(true);
}
