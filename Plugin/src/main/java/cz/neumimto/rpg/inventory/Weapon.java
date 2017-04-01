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
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class Weapon extends Charm {

    public static Weapon EmptyHand = new Weapon(null);
    protected double damage;
    protected boolean current;
    private ItemStack itemStack;
    private int level;
    private Map<ItemRestriction,Object> restrictionSet = new HashMap<>();

    public Weapon(ItemStack itemStack) {
        this.itemStack = itemStack;
        type = HotbarObjectTypes.WEAPON;
    }

    public Map<ItemRestriction,Object> getRestrictions() {
        return restrictionSet;
    }

    public ItemType getItemType() {
        return itemStack.getItem();
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
        return itemStack.getItem() == ItemTypes.SHIELD;
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

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack i) {
        itemStack = i;
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
