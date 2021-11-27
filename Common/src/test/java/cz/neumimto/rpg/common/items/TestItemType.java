package cz.neumimto.rpg.common.items;

public class TestItemType extends RpgItemTypeImpl {
    public TestItemType(String id, String modelName, ItemClass itemClass, double damage, double armor) {
        super(id, modelName, itemClass, damage, armor, null);
    }
}
