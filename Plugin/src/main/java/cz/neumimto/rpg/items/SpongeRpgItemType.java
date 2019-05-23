package cz.neumimto.rpg.items;

import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;

public final class SpongeRpgItemType extends RpgItemTypeImpl {

    private final String itemType;

    public SpongeRpgItemType(String id, String modelName, ItemClass itemClass, double damage, double armor, String itemType) {
        super(id, modelName, itemClass, damage, armor);
        this.itemType = itemType;
    }

    public String getItemType() {
        return itemType;
    }
}
