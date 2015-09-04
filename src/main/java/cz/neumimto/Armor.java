package cz.neumimto;

import org.spongepowered.api.item.ItemType;

/**
 * Created by ja on 4.9.2015.
 */
public class Armor extends Weapon {

    public Armor(ItemType itemType) {
        super(itemType);
        isShield = false;
        damage = 0;
    }

}
