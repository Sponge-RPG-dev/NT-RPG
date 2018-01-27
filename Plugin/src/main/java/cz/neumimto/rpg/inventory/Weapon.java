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

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.XORShiftRnd;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class Weapon extends Charm {

	public static Weapon EmptyHand;
	protected double minDamage;
	protected double maxDamage;
	protected boolean current;
	private RPGItemType itemType;

	private static XORShiftRnd rnd;
	static {
		EmptyHand = new Weapon(ItemStack.empty());
		rnd = new XORShiftRnd();
	}

	public Weapon(ItemStack itemStack) {
		super(itemStack);
		this.itemType = RPGItemType.from(itemStack);
		type = HotbarObjectTypes.WEAPON;
	}

	public RPGItemType getItemType() {
		return itemType;
	}


	public double getDamage() {
		if (maxDamage != 0D) {
			return minDamage + rnd.nextDouble(maxDamage - minDamage);
		}
		return 0D;
	}


	public boolean isShield() {
		return getItemType() == ItemTypes.SHIELD;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	@Override
	public void onRightClick(IActiveCharacter character) {
		if (!current) {
			NtRpgPlugin.GlobalScope.inventorySerivce.changeEquipedWeapon(character, this);
		}
	}

	@Override
	public void onLeftClick(IActiveCharacter character) {
		if (!current) {
			NtRpgPlugin.GlobalScope.inventorySerivce.changeEquipedWeapon(character, this);
		}
	}

	public double getMinDamage() {
		return minDamage;
	}

	public void setMinDamage(double minDamage) {
		this.minDamage = minDamage;
	}

	public double getMaxDamage() {
		return maxDamage;
	}

	public void setMaxDamage(double maxDamage) {
		this.maxDamage = maxDamage;
	}

	@Override
	public IEffectSource getType() {
		return EffectSourceType.WEAPON;
	}
}
