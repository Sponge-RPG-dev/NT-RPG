package cz.neumimto.rpg.common.configuration;

public class Sum implements ItemDamageProcessor {

    @Override
    public double get(double classDamage, double itemDamage) {
        return classDamage + itemDamage;
    }
}
