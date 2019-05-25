package cz.neumimto.rpg.sponge.configuration.itemDamage;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public interface ItemDamageProcessor {

    double get(double classDamage, double itemDamage);
}
