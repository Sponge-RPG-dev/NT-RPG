package cz.neumimto.rpg.inventory.items.subtypes;

import org.spongepowered.api.CatalogType;

public class ItemSubtype implements CatalogType {

    private final String name;
    private final String id;

    public ItemSubtype(String name) {
        this.id = name.toLowerCase();
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}
