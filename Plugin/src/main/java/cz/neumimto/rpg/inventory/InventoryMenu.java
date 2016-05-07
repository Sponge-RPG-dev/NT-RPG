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

package cz.neumimto.rpg.inventory;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.type.Inventory2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 22.7.2015.
 */
public class InventoryMenu {
    private List<InventoryMenuConsumer> consumers = new ArrayList<>();
    private List<ItemStack> itemStacks = new ArrayList<>();
    private List<ItemIndex> itemloc = new ArrayList<>();
    private Inventory2D inventory;

    public InventoryMenu(Inventory2D inventory) {
        this.inventory = inventory;
    }

    public InventoryMenu next(int row, int column, ItemStack itemStack, InventoryMenuConsumer consumer) {
        itemStacks.add(itemStack);
        consumers.add(consumer);
        ItemIndex index = new ItemIndex();
        index.x = row;
        index.y = column;
        index.index = itemloc.size();
        itemloc.add(index);
        inventory.set(new SlotPos(row, column), itemStack);
        return this;
    }

    public InventoryMenuConsumer getConsumer(int x, int y) {
        for (ItemIndex index : itemloc) {
            if (index.x == x && index.y == y) {
                if (consumers.size() <= index.index) {
                    return consumers.get(index.index);
                }
            }
        }
        return InventoryMenuConsumer.EMPTY;
    }

    private class ItemIndex {
        int x, y, index;
    }

}
