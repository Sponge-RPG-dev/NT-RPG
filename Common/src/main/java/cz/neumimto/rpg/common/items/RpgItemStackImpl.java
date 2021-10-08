package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.effects.EffectParams;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;

import java.util.Map;

public class RpgItemStackImpl implements RpgItemStack {

    protected RpgItemType rpgItemType;
    protected Map<IGlobalEffect, EffectParams> enchantments;
    private Map<AttributeConfig, Integer> bonusAttributes;
    private Map<AttributeConfig, Integer> minimalAttributeRequirements;
    private Map<ClassDefinition, Integer> classRequirements;
    private Map<String, Double> itemData;

    public RpgItemStackImpl(RpgItemType rpgItemType,
                            Map<IGlobalEffect, EffectParams> enchantments,
                            Map<AttributeConfig, Integer> bonusAttributes,
                            Map<AttributeConfig, Integer> minimalAttributeRequirements,
                            Map<ClassDefinition, Integer> classRequirements,
                            Map<String, Double> itemData
    ) {
        this.rpgItemType = rpgItemType;
        this.enchantments = enchantments;
        this.bonusAttributes = bonusAttributes;
        this.minimalAttributeRequirements = minimalAttributeRequirements;
        this.classRequirements = classRequirements;
        this.itemData = itemData;
    }

    @Override
    public RpgItemType getItemType() {
        return rpgItemType;
    }

    @Override
    public Map<IGlobalEffect, EffectParams> getEnchantments() {
        return enchantments;
    }

    @Override
    public Map<AttributeConfig, Integer> getMinimalAttributeRequirements() {
        return minimalAttributeRequirements;
    }

    @Override
    public Map<AttributeConfig, Integer> getBonusAttributes() {
        return bonusAttributes;
    }

    @Override
    public Map<ClassDefinition, Integer> getClassRequirements() {
        return classRequirements;
    }

    @Override
    public Map<String, Double> getItemData() {
        return itemData;
    }

    @Override
    public String toString() {
        return "RpgItemStackImpl{" +
                "rpgItemType=" + rpgItemType +
                '}';
    }
}
