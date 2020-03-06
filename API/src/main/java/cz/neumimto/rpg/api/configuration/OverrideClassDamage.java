package cz.neumimto.rpg.api.configuration;

public class OverrideClassDamage implements ItemDamageProcessor {
    @Override
    public double get(double classDamage, double itemDamage) {
        return itemDamage;
    }
}
