package cz.neumimto.rpg.spigot.persistance;

import cz.neumimto.rpg.api.persistance.model.EquipedSlot;

public final class SpigotEquipedSlot implements EquipedSlot {

    private final int slotIndex;

    public SpigotEquipedSlot(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public static EquipedSlot from(int slot) {
        return new SpigotEquipedSlot(slot);
    }

    @Override
    public int getSlotIndex() {
        return slotIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SpigotEquipedSlot that = (SpigotEquipedSlot) o;

        if (slotIndex != that.slotIndex) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 71 + slotIndex;
    }
}

