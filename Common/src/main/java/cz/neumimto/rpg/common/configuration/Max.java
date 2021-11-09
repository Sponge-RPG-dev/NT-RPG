package cz.neumimto.rpg.common.configuration;

public class Max implements ItemDamageProcessor {

    @Override
    public double get(double classDamage, double itemDamage) {
        return Math.max(classDamage, itemDamage);
    }
}
