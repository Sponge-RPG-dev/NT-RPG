package cz.neumimto.rpg.common.items;

/**
 * Created by NeumimTo on 30.3.2018.
 */
public class ItemMetaType {

    private final String name;
    private final String id;

    public ItemMetaType(String name) {
        this.id = "nt-rpg:" + name.toLowerCase();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
