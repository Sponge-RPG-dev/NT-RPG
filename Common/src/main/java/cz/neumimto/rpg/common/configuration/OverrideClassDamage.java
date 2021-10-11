package cz.neumimto.rpg.common.configuration;

public class OverrideClassDamage implements ItemDamageProcessor {
    @Override
    public double get(double classDamage, double itemDamage) {
        return itemDamage;
    }
}
