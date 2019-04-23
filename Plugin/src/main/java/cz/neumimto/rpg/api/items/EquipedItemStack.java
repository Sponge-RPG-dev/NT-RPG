package cz.neumimto.rpg.api.items;

import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.effects.IEffectSourceProvider;

public interface EquipedItemStack extends IEffectSourceProvider {
    RpgItemStack getRpgItemStack();
    ManagedSlot getSlot();
}
