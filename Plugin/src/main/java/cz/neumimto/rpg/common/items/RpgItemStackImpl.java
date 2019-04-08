package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;

import java.util.Map;

public class RpgItemStackImpl implements RpgItemStack {

    protected RpgItemType rpgItemType;
    protected Map<IGlobalEffect, EffectParams> enchantments;

    public RpgItemStackImpl(RpgItemType rpgItemType, Map<IGlobalEffect, EffectParams> enchantments) {
        this.rpgItemType = rpgItemType;
        this.enchantments = enchantments;

    }

    @Override
    public RpgItemType getItemType() {
        return rpgItemType;
    }

    @Override
    public Map<IGlobalEffect, EffectParams> getEnchantments() {
        return enchantments;
    }

}
