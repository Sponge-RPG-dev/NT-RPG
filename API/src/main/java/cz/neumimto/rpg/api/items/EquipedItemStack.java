package cz.neumimto.rpg.api.items;

import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.inventory.ManagedSlot;

public interface EquipedItemStack extends IEffectSourceProvider {
    RpgItemStack getRpgItemStack();

    ManagedSlot getSlot();
}
