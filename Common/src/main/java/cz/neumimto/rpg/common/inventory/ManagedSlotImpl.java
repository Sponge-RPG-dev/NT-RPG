package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.items.RpgItemStack;

import java.util.Optional;

public class ManagedSlotImpl implements ManagedSlot {

    private final int id;

    private RpgItemStack content;

    public ManagedSlotImpl(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Optional<RpgItemStack> getContent() {
        return Optional.ofNullable(content);
    }

    @Override
    public void setContent(RpgItemStack content) {
        this.content = content;
    }
}
