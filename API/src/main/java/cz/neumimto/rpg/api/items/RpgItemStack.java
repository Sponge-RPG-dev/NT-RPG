package cz.neumimto.rpg.api.items;

import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;

import java.util.Map;

public interface RpgItemStack {

    RpgItemType getItemType();

    Map<IGlobalEffect, EffectParams> getEnchantments();

    Map<AttributeConfig, Integer> getMinimalAttributeRequirements();

    default double getDamage() {
        return getItemType().getDamage();
    }

    default double getArmor() {
        return getItemType().getArmor();
    }

    Map<AttributeConfig, Integer> getBonusAttributes();

    Map<ClassDefinition, Integer> getClassRequirements();
}
