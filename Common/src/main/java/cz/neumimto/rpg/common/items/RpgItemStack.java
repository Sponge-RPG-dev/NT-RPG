package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.effects.EffectParams;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;

import java.util.Map;

public interface RpgItemStack {

    RpgItemType getItemType();

    Map<IGlobalEffect, EffectParams> getEnchantments();

    Map<AttributeConfig, Integer> getMinimalAttributeRequirements();


    Map<AttributeConfig, Integer> getBonusAttributes();

    Map<ClassDefinition, Integer> getClassRequirements();

    Map<String, Double> getItemData();
}
