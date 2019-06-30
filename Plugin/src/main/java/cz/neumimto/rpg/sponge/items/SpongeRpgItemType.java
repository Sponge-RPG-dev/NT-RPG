package cz.neumimto.rpg.sponge.items;

import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;
import org.spongepowered.api.item.ItemType;

public final class SpongeRpgItemType extends RpgItemTypeImpl {

    private final ItemType itemType;

    public SpongeRpgItemType(String id, String modelName, ItemClass itemClass, double damage, double armor, ItemType itemType) {
        super(id, modelName, itemClass, damage, armor);
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
