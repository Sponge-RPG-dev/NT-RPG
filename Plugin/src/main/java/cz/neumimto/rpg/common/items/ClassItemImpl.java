package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.RpgItemType;

public class ClassItemImpl implements ClassItem {

    private final RpgItemType type;
    private final double damage;
    private final double  armor;

    public ClassItemImpl(RpgItemType type, double damage, double armor) {
        this.type = type;
        this.damage = damage;
        this.armor = armor;
    }

    @Override
    public RpgItemType getType() {
        return type;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public double getArmor() {
        return armor;
    }
}
