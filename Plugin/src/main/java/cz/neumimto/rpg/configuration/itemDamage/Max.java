package cz.neumimto.rpg.configuration.itemDamage;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Max implements ItemDamageProcessor {

    @Override
    public double get(double classDamage, double itemDamage) {
        return Math.max(classDamage, itemDamage);
    }
}
