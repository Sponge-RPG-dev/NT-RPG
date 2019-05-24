package cz.neumimto.rpg.persistance.model;

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

/**
 * Created by NeumimTo on 20.5.2018.
 */
public final class EquipedSlot {

    private final String className;
    private final transient Class<?> runtimeInventoryClass;
    private final int slotIndex;

    public EquipedSlot(String className, int slotIndex) throws ClassNotFoundException {
        this.slotIndex = slotIndex;
        this.className = className;
        runtimeInventoryClass = Class.forName(className);
    }

    public EquipedSlot(String className, int slotIndex, Class<?> class_) {
        this.slotIndex = slotIndex;
        this.className = className;
        this.runtimeInventoryClass = class_;
    }

    public static EquipedSlot from(Slot slot) {
        Slot transform = slot.transform();
        Class<? extends Inventory> aClass = transform.parent().getClass();
        SlotIndex slotIndex = transform.getInventoryProperty(SlotIndex.class).get();
        return new EquipedSlot(
                aClass.getName(),
                slotIndex.getValue(),
                aClass
        );
    }

    public String getClassName() {
        return className;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Inventory> getRuntimeInventoryClass() {
        return (Class<? extends Inventory>) runtimeInventoryClass;
    }

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

        EquipedSlot that = (EquipedSlot) o;

        if (slotIndex != that.slotIndex) {
            return false;
        }
        if (!className.equals(that.className)) {
            return false;
        }
        return runtimeInventoryClass != null ? runtimeInventoryClass.equals(that.runtimeInventoryClass) : that.runtimeInventoryClass == null;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 71 * result + slotIndex;
        return result;
    }
}
