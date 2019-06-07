package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.sponge.inventory.items.subtypes.ItemSubtype;
import cz.neumimto.rpg.sponge.inventory.items.subtypes.ItemSubtypes;

public class SlotEffectSource implements IEffectSource {

    public static final SlotEffectSource OFF_HAND = new SlotEffectSource(-100, ItemSubtypes.ANY);
    public static final SlotEffectSource MAIN_HAND = new SlotEffectSource(-99, ItemSubtypes.ANY);

    private final int slotId;
    private final ItemSubtype itemSubtype;

    public SlotEffectSource(int slotId, ItemSubtype itemSubtype) {
        this.slotId = slotId;
        this.itemSubtype = itemSubtype;
    }

    public int getSlotId() {
        return slotId;
    }

    public ItemSubtype getAcceptsItemSubtype() {
        return itemSubtype;
    }

    @Override
    public boolean multiple() {
        return false;
    }
}
