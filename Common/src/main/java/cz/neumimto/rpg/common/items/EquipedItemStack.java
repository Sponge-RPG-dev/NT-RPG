package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.common.effects.IEffectSourceProvider;
import cz.neumimto.rpg.common.inventory.ManagedSlot;

public interface EquipedItemStack extends IEffectSourceProvider {
    RpgItemStack getRpgItemStack();

    ManagedSlot getSlot();
}
