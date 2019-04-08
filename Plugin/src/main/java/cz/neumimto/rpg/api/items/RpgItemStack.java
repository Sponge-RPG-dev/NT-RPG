package cz.neumimto.rpg.api.items;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.attributes.Attribute;

import java.util.Map;

public interface RpgItemStack {

    RpgItemType getItemType();

    Map<IGlobalEffect, EffectParams> getEnchantments();

    Map<Attribute, Integer> getMinimalAttributeRequirements();

    default double getDamage() {
        return getItemType().getDamage();
    }

    default double getArmor() {
        return getItemType().getArmor();
    }

    Map<Attribute, Integer>  getBonusAttributes();
}
