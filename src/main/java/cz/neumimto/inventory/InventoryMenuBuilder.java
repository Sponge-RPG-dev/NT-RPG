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

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 22.7.2015.
 */
public class InventoryMenuBuilder {

    private Set<Option> options = new HashSet<>();

    public static InventoryMenuBuilder create() {
        return new InventoryMenuBuilder();
    }

    public InventoryMenuBuilder addOption(int row, int column, ItemStack i, InventoryMenuConsumer consumer) {
        if (i == null)
            throw new IllegalArgumentException("itemStack cannot be null");
        if (consumer == null)
            throw new IllegalArgumentException("consumer cannot be null");
        options.add(new Option(row, column, i, consumer));
        return this;
    }

    public InventoryMenu build() {
        InventoryMenu inventoryMenu = new InventoryMenu(null);
        for (Option o : options) {
            inventoryMenu.next(o.getRow(), o.getColumn(), o.getItemStack(), o.getConsumer());
        }
        return inventoryMenu;
    }
}
