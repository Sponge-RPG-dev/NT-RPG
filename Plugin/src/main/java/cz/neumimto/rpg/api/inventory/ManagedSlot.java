package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.items.RpgItemStack;

public interface ManagedSlot {

    int getId();

    RpgItemStack getContent();
}
