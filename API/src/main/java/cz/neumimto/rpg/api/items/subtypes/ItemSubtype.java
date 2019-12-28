package cz.neumimto.rpg.api.items.subtypes;

public class ItemSubtype {

    private final String name;
    private final String id;

    public ItemSubtype(String name) {
        this.id = name.toLowerCase();
        this.name = name;
    }


    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }
}
