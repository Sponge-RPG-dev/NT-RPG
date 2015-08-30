package cz.neumimto;

import cz.neumimto.effects.IEffect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class Weapon {
    public static Weapon EmptyHand = new Weapon();
    private float damage;
    private Map<IEffect, Integer> effects = new HashMap<>();
    private byte slot;

    public byte getSlot() {
        return slot;
    }

    public void setSlot(byte slot) {
        this.slot = slot;
    }

    public void setDamage(Character c) {

    }

    public void setDamage(float f) {
        this.damage = f;
    }
}
