package cz.neumimto.rpg.api.configuration;

public class OverrideItemDamage implements ItemDamageProcessor {

    public double get(double classDamage, double itemDamage) {
        return classDamage;
    }
}
