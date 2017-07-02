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

import cz.neumimto.rpg.effects.IEffectSource;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 4.9.2015.
 */
public class Armor extends Weapon {

    private IEffectSource armorType;

    public Armor(ItemStack itemStack, IEffectSource armorType) {
        super(itemStack);
        this.armorType = armorType;
    }

    @Override
    public IEffectSource getType() {
        return armorType;
    }

    public static Armor NONE = null;
}
