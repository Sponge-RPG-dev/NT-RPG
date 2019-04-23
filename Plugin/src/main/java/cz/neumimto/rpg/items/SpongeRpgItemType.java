package cz.neumimto.rpg.items;

import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;
import org.spongepowered.api.item.ItemType;

public final class SpongeRpgItemType extends RpgItemTypeImpl {

    private final ItemType itemType;

    public SpongeRpgItemType(String id, String modelName, WeaponClass weaponClass, double damage, double armor, ItemType itemType) {
        super(id, modelName, weaponClass, damage, armor);
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
