package cz.neumimto.inventory;

import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.CharacterService;
import org.spongepowered.api.Game;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.inventory.type.GridInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class InventoryService {

    @Inject
    private Game game;

    @Inject
    private CharacterService characterService;


    private Map<UUID, InventoryMenu> inventoryMenus = new HashMap<>();

    public ItemStack getHelpItem(List<String> lore, ItemType type) {
        ItemStackBuilder builder = game.getRegistry().createItemBuilder();
        builder.quantity(1).itemType(type);
        return builder.build();
    }

    public Map<UUID, InventoryMenu> getInventoryMenus() {
        return inventoryMenus;
    }

    public void addInventoryMenu(UUID uuid, InventoryMenu menu) {
        if (!inventoryMenus.containsKey(uuid))
            inventoryMenus.put(uuid, menu);
    }

    public InventoryMenu getInventoryMenu(UUID uniqueId) {
        return inventoryMenus.get(uniqueId);
    }

    public GridInventory getInventory(UUID uniqueId) {
        return null;
    }

}
