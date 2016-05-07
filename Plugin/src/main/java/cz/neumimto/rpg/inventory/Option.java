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

/**
 * Created by NeumimTo on 22.7.2015.
 */
public class Option {
    private int row, column;
    private ItemStack itemStack;
    private InventoryMenuConsumer consumer;

    public Option(int row, int column, ItemStack itemStack, InventoryMenuConsumer consumer) {
        this.row = row;
        this.column = column;
        this.itemStack = itemStack;
        this.consumer = consumer;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public InventoryMenuConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(InventoryMenuConsumer consumer) {
        this.consumer = consumer;
    }
}
