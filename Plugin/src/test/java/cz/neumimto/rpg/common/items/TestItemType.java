package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.WeaponClass;

public class TestItemType extends RpgItemTypeImpl {
    public TestItemType(String id, String modelName, WeaponClass weaponClass, double damage, double armor) {
        super(id, modelName, weaponClass, damage, armor);
    }
}
