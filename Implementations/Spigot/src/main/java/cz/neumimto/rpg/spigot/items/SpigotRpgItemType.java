package cz.neumimto.rpg.spigot.items;

import cz.neumimto.rpg.common.items.ItemClass;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;
import org.bukkit.Material;

public class SpigotRpgItemType extends RpgItemTypeImpl {

    private final Material material;

    public SpigotRpgItemType(String id, String modelName, ItemClass itemClass,Material material, String permission) {
        super(id, modelName, itemClass, permission);
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }
}
