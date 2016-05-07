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
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class Weapon extends HotbarObject {

    public static Weapon EmptyHand = new Weapon(null);
    private final ItemType itemType;
    protected double damage;
    protected boolean isShield;
    protected boolean current;
    private Map<IGlobalEffect, Integer> effects = new HashMap<>();
    private ItemStack itemStack;
    private boolean isRuneword;
    private int level;

    public Weapon(ItemType itemType) {
        this.itemType = itemType;
        type = HotbarObjectTypes.WEAPON;
    }

    public ItemType getItemType() {
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
        return isShield;
    }

    public void setShield(boolean shield) {
        isShield = shield;
    }

    public Map<IGlobalEffect, Integer> getEffects() {
        return effects;
    }

    public void setEffects(Map<IGlobalEffect, Integer> effects) {
        this.effects = effects;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean isRuneword() {
        return isRuneword;
    }

    public void setRuneword(boolean runeword) {
        isRuneword = runeword;
    }

    @Override
    public void onRightClick(IActiveCharacter character) {
        if (!current)
            NtRpgPlugin.GlobalScope.inventorySerivce.changeEquipedWeapon(character, this);
        else if (character.isSocketing()) {
            NtRpgPlugin.GlobalScope.inventorySerivce.insertRune(character);
        }
    }

    @Override
    public void onLeftClick(IActiveCharacter character) {
        if (!current)
            NtRpgPlugin.GlobalScope.inventorySerivce.changeEquipedWeapon(character, this);

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
}
