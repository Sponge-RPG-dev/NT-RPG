package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.attributes.Attribute;

import java.util.Map;

public class RpgItemStackImpl implements RpgItemStack {

    protected RpgItemType rpgItemType;
    protected Map<IGlobalEffect, EffectParams> enchantments;
    private Map<Attribute, Integer> bonusAttributes;
    private Map<Attribute, Integer> minimalAttributeRequirements;

    public RpgItemStackImpl(RpgItemType rpgItemType, Map<IGlobalEffect, EffectParams> enchantments, Map<Attribute, Integer> bonusAttributes, Map<Attribute, Integer> minimalAttributeRequirements) {
        this.rpgItemType = rpgItemType;
        this.enchantments = enchantments;
        this.bonusAttributes = bonusAttributes;
        this.minimalAttributeRequirements = minimalAttributeRequirements;
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
    public Map<Attribute, Integer> getMinimalAttributeRequirements() {
        return minimalAttributeRequirements;
    }

    @Override
    public Map<Attribute, Integer> getBonusAttributes() {
        return bonusAttributes;
    }

    @Override
    public String toString() {
        return "RpgItemStackImpl{" +
                "rpgItemType=" + rpgItemType +
                '}';
    }
}
