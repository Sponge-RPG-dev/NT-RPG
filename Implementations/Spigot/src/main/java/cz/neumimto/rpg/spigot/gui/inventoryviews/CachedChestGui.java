package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.InventoryComponent;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CachedChestGui extends ChestGui  {
    /**
     * Constructs a new chest GUI
     *
     * @param rows  the amount of rows this gui should contain, in range 1..6.
     * @param title the title/name of this gui.
     * @since 0.8.0
     */
    private Inventory theInventory;
    public CachedChestGui(int rows, @NotNull String title) {
        super(rows, title);
    }

    @Override
    public void show(@NotNull HumanEntity humanEntity) {
        if (theInventory == null) {
            theInventory = getInventory();
            int height = getInventoryComponent().getHeight();

            InventoryComponent topComponent = getInventoryComponent().excludeRows(height - 4, height - 1);

            topComponent.placeItems(theInventory, 0);

        } else {
            humanEntity.openInventory(theInventory);
        }
    }

}
