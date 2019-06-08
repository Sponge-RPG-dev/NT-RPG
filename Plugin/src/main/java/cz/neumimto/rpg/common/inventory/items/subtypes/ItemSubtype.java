package cz.neumimto.rpg.common.inventory.items.subtypes;

public class ItemSubtype {

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
