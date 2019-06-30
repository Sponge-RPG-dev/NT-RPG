package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;

import java.util.Map;

public class RpgItemStackImpl implements RpgItemStack {

    protected RpgItemType rpgItemType;
    protected Map<IGlobalEffect, EffectParams> enchantments;
    private Map<AttributeConfig, Integer> bonusAttributes;
    private Map<AttributeConfig, Integer> minimalAttributeRequirements;
    private Map<ClassDefinition, Integer> classRequirements;

    public RpgItemStackImpl(RpgItemType rpgItemType, Map<IGlobalEffect, EffectParams> enchantments, Map<AttributeConfig, Integer> bonusAttributes, Map<AttributeConfig, Integer> minimalAttributeRequirements, Map<ClassDefinition, Integer> classRequirements) {
        this.rpgItemType = rpgItemType;
        this.enchantments = enchantments;
        this.bonusAttributes = bonusAttributes;
        this.minimalAttributeRequirements = minimalAttributeRequirements;
        this.classRequirements = classRequirements;
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
    public String toString() {
        return "RpgItemStackImpl{" +
                "rpgItemType=" + rpgItemType +
                '}';
    }
}
