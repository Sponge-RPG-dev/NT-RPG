package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.ItemClass;

public class TestItemType extends RpgItemTypeImpl {
    public TestItemType(String id, String modelName, ItemClass itemClass, double damage, double armor) {
        super(id, modelName, itemClass, damage, armor);
    }
}
