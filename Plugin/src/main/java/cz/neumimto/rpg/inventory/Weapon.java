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
import cz.neumimto.rpg.inventory.data.CustomItemData;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class Weapon extends Charm {

	public static Weapon EmptyHand;
	protected double damage;
	protected boolean current;
	private RPGItemType itemType;
	private int level;

	static {
		EmptyHand = new Weapon(ItemStack.empty());
	}

	public Weapon(ItemStack itemStack) {
		super(itemStack);
		this.itemType = RPGItemType.from(itemStack);
		type = HotbarObjectTypes.WEAPON;
	}

	public RPGItemType getItemType() {
		return itemType;
	}

	public void setDamage(float f) {
		this.damage = f;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public boolean isShield() {
		return getItemType() == ItemTypes.SHIELD;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	@Override
	public void onRightClick(IActiveCharacter character) {
		if (character.isSocketing()) {
			NtRpgPlugin.GlobalScope.inventorySerivce.insertRune(character);
		} else if (!current) {
			NtRpgPlugin.GlobalScope.inventorySerivce.changeEquipedWeapon(character, this);
		}
	}

	@Override
	public void onLeftClick(IActiveCharacter character) {
		if (character.isSocketing()) {
			NtRpgPlugin.GlobalScope.inventorySerivce.cancelSocketing(character);
		} else if (!current) {
			NtRpgPlugin.GlobalScope.inventorySerivce.changeEquipedWeapon(character, this);
		}
	}

	public void setItemData(CustomItemData i) {
		customItemData = i;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public IEffectSource getType() {
		return EffectSourceType.WEAPON;
	}
}
