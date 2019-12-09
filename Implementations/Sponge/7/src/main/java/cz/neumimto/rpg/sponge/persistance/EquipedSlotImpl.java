package cz.neumimto.rpg.sponge.persistance;

import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

/**
 * Created by NeumimTo on 20.5.2018.
 */
public final class EquipedSlotImpl implements EquipedSlot {

    private final String className;
    private final transient Class<?> runtimeInventoryClass;
    private final int slotIndex;

    public EquipedSlotImpl(String className, int slotIndex) throws ClassNotFoundException {
        this(className, slotIndex, Class.forName(className));
    }

    public EquipedSlotImpl(String className, int slotIndex, Class<?> class_) {
        this.slotIndex = slotIndex;
        this.className = className;
        this.runtimeInventoryClass = class_;
    }

    public static EquipedSlot from(Slot slot) {
        Slot transform = slot.transform();
        Class<? extends Inventory> aClass = transform.parent().getClass();
        SlotIndex slotIndex = transform.getInventoryProperty(SlotIndex.class).get();
        return new EquipedSlotImpl(
                aClass.getName(),
                slotIndex.getValue(),
                aClass
        );
    }

    public String getClassName() {
        return className;
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

        EquipedSlotImpl that = (EquipedSlotImpl) o;

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

    @Override
    public String toString() {
        return className + "@" + slotIndex;
    }
}
