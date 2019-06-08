package cz.neumimto.rpg.sponge.inventory.items.subtypes;

import cz.neumimto.rpg.common.inventory.items.subtypes.ItemSubtype;
import org.spongepowered.api.CatalogType;

public class ItemSubtypeWrapper implements CatalogType {
    private ItemSubtype itemSubtype;

    public ItemSubtypeWrapper(ItemSubtype extraCatalog) {
        this.itemSubtype = extraCatalog;
    }

    @Override
    public String getId() {
        return itemSubtype.getId();
    }

    @Override
    public String getName() {
        return itemSubtype.getName();
    }
}
