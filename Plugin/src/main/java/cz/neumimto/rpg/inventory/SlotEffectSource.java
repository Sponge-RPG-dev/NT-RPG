package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.inventory.items.subtypes.ItemSubtype;

public class SlotEffectSource implements IEffectSource {

    private final int slotId;
    private final ItemSubtype itemSubtype;

    public SlotEffectSource(int slotId, ItemSubtype itemSubtype) {
        this.slotId = slotId;
        this.itemSubtype = itemSubtype;
    }

    public int getSlotId() {
        return slotId;
    }

    public ItemSubtype getItemSubtype() {
        return itemSubtype;
    }

    @Override
    public boolean multiple() {
        return false;
    }
}
