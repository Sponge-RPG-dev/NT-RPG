package cz.neumimto.rpg.api.configuration;

public class Max implements ItemDamageProcessor {

    @Override
    public double get(double classDamage, double itemDamage) {
        return Math.max(classDamage, itemDamage);
    }
}
