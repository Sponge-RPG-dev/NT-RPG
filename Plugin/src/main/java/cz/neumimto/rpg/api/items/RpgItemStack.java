package cz.neumimto.rpg.api.items;

import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;

import java.util.Map;

public interface RpgItemStack {

    RpgItemType getItemType();

    Map<IGlobalEffect, EffectParams> getEnchantments();

    default double getDamage() {
        return getItemType().getDamage();
    }

    default double getArmor() {
        return getItemType().getArmor();
    }
}
