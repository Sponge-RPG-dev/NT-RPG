package cz.neumimto.rpg.inventory;

/**
 * Created by NeumimTo on 14.1.2018.
 */
public class ItemDamage {
    public double min;
    public double max;

    public ItemDamage() {
    }

    public ItemDamage(double min, double max) {
        this.min = min;
        this.max = max;
    }
}
