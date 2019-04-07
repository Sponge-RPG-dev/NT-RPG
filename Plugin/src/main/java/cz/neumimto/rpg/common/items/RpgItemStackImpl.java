package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.effects.IGlobalEffect;

import java.util.Map;

public class RpgItemStackImpl implements RpgItemStack {

    protected RpgItemType rpgItemType;
    protected Map<String, IGlobalEffect> enchantments;

    @Override
    public RpgItemType getItemType() {
        return rpgItemType;
    }

    @Override
    public Map<String, IGlobalEffect> getEnchantments() {
        return enchantments;
    }

}
