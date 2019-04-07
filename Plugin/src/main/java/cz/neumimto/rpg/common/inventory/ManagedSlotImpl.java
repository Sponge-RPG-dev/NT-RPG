package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.inventory.ManagedSlot;

public abstract class ManagedSlotImpl implements ManagedSlot {

    private int id;

    @Override
    public int getId() {
        return id;
    }

}
