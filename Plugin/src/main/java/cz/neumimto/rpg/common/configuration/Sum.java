package cz.neumimto.rpg.common.configuration;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Sum implements ItemDamageProcessor {

    @Override
    public double get(double classDamage, double itemDamage) {
        return classDamage + itemDamage;
    }
}
