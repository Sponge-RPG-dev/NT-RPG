package cz.neumimto.rpg.common.configuration;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public interface ItemDamageProcessor {

    double get(double classDamage, double itemDamage);
}
