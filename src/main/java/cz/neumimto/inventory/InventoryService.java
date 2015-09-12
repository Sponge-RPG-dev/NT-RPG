/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

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
