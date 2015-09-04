package cz.neumimto;

import cz.neumimto.effects.IGlobalEffect;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class Weapon {

    public static Weapon EmptyHand = new Weapon(null);

    protected double damage;
    protected boolean isShield;
    private final ItemType itemType;
    private Map<IGlobalEffect, Integer> effects = new HashMap<>();
    private byte slot;


    public Weapon(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public byte getSlot() {
        return slot;
    }

    public void setSlot(byte slot) {
        this.slot = slot;
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
}
